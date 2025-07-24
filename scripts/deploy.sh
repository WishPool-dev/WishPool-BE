#!/bin/bash

# 1. 변수 설정
BLUE_PORT=8081
GREEN_PORT=8082
NGINX_CONF="/etc/nginx/conf.d/service-url.inc"
DOCKER_COMPOSE_CMD="/usr/local/bin/docker-compose"

# ✨ [추가] GCP Artifact Registry에 대한 Docker 인증 설정
echo ">>> Configuring Docker for GCR..."
gcloud auth configure-docker ${GAR_LOCATION}-docker.pkg.dev

# .env 파일은 워크플로우에서 이미 생성되었으므로 docker-compose가 자동으로 사용합니다.

# 2. 현재 실행 중인 포트 확인
CURRENT_PORT=$(sudo cat ${NGINX_CONF} | grep -Po '[0-9]+' | tail -1)
echo ">>> Current service port: ${CURRENT_PORT}"

# 3. 새로운 버전(Green)을 띄울 포트 결정
if [ "${CURRENT_PORT}" -eq ${BLUE_PORT} ]; then
  TARGET_PORT=${GREEN_PORT}
  OLD_PORT=${BLUE_PORT}
else
  TARGET_PORT=${BLUE_PORT}
  OLD_PORT=${GREEN_PORT}
fi
echo ">>> New service will be deployed to port ${TARGET_PORT}"

# 4. 새로운 버전(Green)의 Docker 이미지 다운로드 및 컨테이너 실행
export HOST_PORT=${TARGET_PORT} # 포트 매핑을 위해 이 변수는 export 필요
# docker-compose는 현재 디렉토리의 .env 파일을 자동으로 읽습니다.
${DOCKER_COMPOSE_CMD} -p wishpool-app-${TARGET_PORT} -f docker-compose.app.yml pull
${DOCKER_COMPOSE_CMD} -p wishpool-app-${TARGET_PORT} -f docker-compose.app.yml up -d

# 5. 헬스 체크 (시간과 횟수 증가)
echo ">>> Health check started on port ${TARGET_PORT}..."
sleep 20 # 애플리케이션 시작 대기 시간 증가
for i in {1..20}; do
  response_code=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:${TARGET_PORT}/actuator/health)

  if [ ${response_code} -eq 200 ]; then
    echo ">>> Health check successful!"

    # 6. Nginx 트래픽 전환
    echo "set \$service_url http://127.0.0.1:${TARGET_PORT};" | sudo tee ${NGINX_CONF}
    sudo nginx -s reload
    echo ">>> Traffic switched to port ${TARGET_PORT}."

    # 7. 기존 버전(Old) 컨테이너 종료
    echo ">>> Stopping old container on port ${OLD_PORT}..."
    ${DOCKER_COMPOSE_CMD} -p wishpool-app-${OLD_PORT} -f docker-compose.app.yml down
    echo ">>> Old container on port ${OLD_PORT} stopped."
    exit 0
  fi

  echo ">>> Health check failed (status: ${response_code}). Retrying in 10 seconds... (${i}/20)"
  sleep 10
done

# 8. 헬스 체크 최종 실패 시
echo ">>> Deployment failed. Health check did not pass."
echo ">>> Printing logs from failed container..."
${DOCKER_COMPOSE_CMD} -p wishpool-app-${TARGET_PORT} -f docker-compose.app.yml logs --tail="200"

# 실패한 컨테이너 정리
echo ">>> Cleaning up failed deployment..."
${DOCKER_COMPOSE_CMD} -p wishpool-app-${TARGET_PORT} -f docker-compose.app.yml down

exit 1