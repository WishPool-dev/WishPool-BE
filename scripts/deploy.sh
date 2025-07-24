#!/bin/bash

# --- 스크립트 시작 및 인자 확인 ---
echo "======================================="
echo "        STARTING DEPLOYMENT"
echo "======================================="

if [ -z "$1" ] || [ -z "$2" ]; then
    echo "FATAL ERROR: Missing arguments."
    echo "Usage: ./deploy.sh <GAR_LOCATION> <ENV_FILE_PATH>"
    exit 1
fi

GAR_LOCATION=$1
APP_ENV_FILE=$2

echo ">> GAR_LOCATION received: ${GAR_LOCATION}"
echo ">> ENV_FILE_PATH received: ${APP_ENV_FILE}"

if [ ! -f "${APP_ENV_FILE}" ]; then
    echo "FATAL ERROR: Environment file not found at: ${APP_ENV_FILE}"
    exit 1
fi
echo "---------------------------------------"


# ✨ [핵심] 1. 공유 Docker 네트워크 생성 및 확인
NETWORK_NAME="wishpool-net"
if [ -z $(docker network ls --filter name=^${NETWORK_NAME}$ --format="{{ .Name }}") ] ; then
    echo ">>> Network '${NETWORK_NAME}' not found. Creating network..."
    docker network create ${NETWORK_NAME}
    echo ">>> Network '${NETWORK_NAME}' created successfully."
else
    echo ">>> Network '${NETWORK_NAME}' already exists."
fi
echo "---------------------------------------"


# 2. 변수 설정
BLUE_PORT=8081
GREEN_PORT=8082
NGINX_CONF="/etc/nginx/conf.d/service-url.inc"
DOCKER_COMPOSE_CMD="/usr/local/bin/docker-compose"


# 3. GCP Artifact Registry Docker 인증
echo ">>> Configuring Docker for GCR..."
gcloud auth configure-docker ${GAR_LOCATION}-docker.pkg.dev


# 4. 현재 실행 중인 포트 확인
CURRENT_PORT=$(sudo cat ${NGINX_CONF} | grep -Po '[0-9]+' | tail -1)
echo ">>> Current service port: ${CURRENT_PORT}"


# 5. 새로운 버전(Green)을 띄울 포트 결정
if [ "${CURRENT_PORT}" -eq ${BLUE_PORT} ]; then
  TARGET_PORT=${GREEN_PORT}
  OLD_PORT=${BLUE_PORT}
else
  TARGET_PORT=${BLUE_PORT}
  OLD_PORT=${GREEN_PORT}
fi
echo ">>> New service will be deployed to port ${TARGET_PORT}"


# 6. 새로운 버전(Green)의 Docker 이미지 다운로드 및 컨테이너 실행
export HOST_PORT=${TARGET_PORT}

echo ">>> Pulling new docker image..."
${DOCKER_COMPOSE_CMD} --env-file "${APP_ENV_FILE}" -p wishpool-app-${TARGET_PORT} -f docker-compose.app.yml pull

echo ">>> Starting new container..."
${DOCKER_COMPOSE_CMD} --env-file "${APP_ENV_FILE}" -p wishpool-app-${TARGET_PORT} -f docker-compose.app.yml up -d


# 7. 헬스 체크
echo ">>> Health check started on port ${TARGET_PORT}..."
sleep 20
for i in {1..7}; do
  response_code=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:${TARGET_PORT}/actuator/health)
  if [ ${response_code} -eq 200 ] || [ ${response_code} -eq 401 ]; then
     echo ">>> Health check successful!"
     echo "set \$service_url http://127.0.0.1:${TARGET_PORT};" | sudo tee ${NGINX_CONF}
     sudo nginx -s reload
     echo ">>> Traffic switched to port ${TARGET_PORT}."
     echo ">>> Stopping old container on port ${OLD_PORT}..."
     ${DOCKER_COMPOSE_CMD} --env-file "${APP_ENV_FILE}" -p wishpool-app-${OLD_PORT} -f docker-compose.app.yml down
     echo ">>> Old container on port ${OLD_PORT} stopped."
     exit 0
   fi
  echo ">>> Health check failed (status: ${response_code}). Retrying in 10 seconds... (${i}/7)"
  sleep 10
done


# 8. 헬스 체크 최종 실패 시
echo ">>> Deployment failed. Health check did not pass."
echo ">>> Printing logs from failed container..."
${DOCKER_COMPOSE_CMD} --env-file "${APP_ENV_FILE}" -p wishpool-app-${TARGET_PORT} -f docker-compose.app.yml logs --tail="200"
echo ">>> Cleaning up failed deployment..."
${DOCKER_COMPOSE_CMD} --env-file "${APP_ENV_FILE}" -p wishpool-app-${TARGET_PORT} -f docker-compose.app.yml down
exit 1