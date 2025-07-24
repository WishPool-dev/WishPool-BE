#!/bin/bash

# ✨ [핵심] 스크립트 파일이 위치한 디렉토리의 절대 경로를 찾습니다.
# 이 코드는 스크립트가 어디서 실행되든 항상 정확한 경로를 보장합니다.
SCRIPT_DIR=$( cd -- "$( dirname -- "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )

# ✨ 스크립트 디렉토리의 부모 디렉토리(프로젝트 루트)에 있는 .env.app 파일의 절대 경로를 설정합니다.
APP_ENV_FILE="${SCRIPT_DIR}/../.env.app"

# --- 최종 디버깅: 계산된 경로가 올바른지 확인 ---
echo "======================================="
echo "        STARTING FINAL DEPLOY          "
echo "======================================="
echo ">> Script's own directory (SCRIPT_DIR): ${SCRIPT_DIR}"
echo ">> Absolute path for env file (APP_ENV_FILE): ${APP_ENV_FILE}"

if [ -f "${APP_ENV_FILE}" ]; then
    echo ">> .env.app file FOUND. Contents:"
    cat "${APP_ENV_FILE}"
else
    echo ">> FATAL ERROR: .env.app file NOT FOUND at ${APP_ENV_FILE}"
    exit 1
fi
echo "---------------------------------------"


# 1. 변수 설정
BLUE_PORT=8081
GREEN_PORT=8082
NGINX_CONF="/etc/nginx/conf.d/service-url.inc"
DOCKER_COMPOSE_CMD="/usr/local/bin/docker-compose"

# 2. GCP Artifact Registry Docker 인증
echo ">>> Configuring Docker for GCR..."
gcloud auth configure-docker ${GAR_LOCATION}-docker.pkg.dev

# 3. 현재 실행 중인 포트 확인
CURRENT_PORT=$(sudo cat ${NGINX_CONF} | grep -Po '[0-9]+' | tail -1)
echo ">>> Current service port: ${CURRENT_PORT}"

# 4. 새로운 버전(Green)을 띄울 포트 결정
if [ "${CURRENT_PORT}" -eq ${BLUE_PORT} ]; then
  TARGET_PORT=${GREEN_PORT}
  OLD_PORT=${BLUE_PORT}
else
  TARGET_PORT=${BLUE_PORT}
  OLD_PORT=${GREEN_PORT}
fi
echo ">>> New service will be deployed to port ${TARGET_PORT}"

# 5. 새로운 버전(Green)의 Docker 이미지 다운로드 및 컨테이너 실행
export HOST_PORT=${TARGET_PORT}

echo ">>> Pulling new docker image..."
${DOCKER_COMPOSE_CMD} --env-file "${APP_ENV_FILE}" -p wishpool-app-${TARGET_PORT} -f docker-compose.app.yml pull

echo ">>> Starting new container..."
${DOCKER_COMPOSE_CMD} --env-file "${APP_ENV_FILE}" -p wishpool-app-${TARGET_PORT} -f docker-compose.app.yml up -d

# 6. 헬스 체크
echo ">>> Health check started on port ${TARGET_PORT}..."
sleep 20
for i in {1..20}; do
  response_code=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:${TARGET_PORT}/actuator/health)

  if [ ${response_code} -eq 200 ]; then
    echo ">>> Health check successful!"

    # 7. Nginx 트래픽 전환 등 나머지 로직은 동일...
    echo "set \$service_url http://127.0.0.1:${TARGET_PORT};" | sudo tee ${NGINX_CONF}
    sudo nginx -s reload
    echo ">>> Traffic switched to port ${TARGET_PORT}."

    echo ">>> Stopping old container on port ${OLD_PORT}..."
    ${DOCKER_COMPOSE_CMD} --env-file "${APP_ENV_FILE}" -p wishpool-app-${OLD_PORT} -f docker-compose.app.yml down
    echo ">>> Old container on port ${OLD_PORT} stopped."
    exit 0
  fi

  echo ">>> Health check failed (status: ${response_code}). Retrying in 10 seconds... (${i}/20)"
  sleep 10
done

# 9. 헬스 체크 최종 실패 시
echo ">>> Deployment failed. Health check did not pass."
echo ">>> Printing logs from failed container..."
${DOCKER_COMPOSE_CMD} --env-file "${APP_ENV_FILE}" -p wishpool-app-${TARGET_PORT} -f docker-compose.app.yml logs --tail="200"

echo ">>> Cleaning up failed deployment..."
${DOCKER_COMPOSE_CMD} --env-file "${APP_ENV_FILE}" -p wishpool-app-${TARGET_PORT} -f docker-compose.app.yml down

exit 1