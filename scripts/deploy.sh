#!/bin/bash

# 1. 변수 설정
BLUE_PORT=8081
GREEN_PORT=8082
# Nginx 설정 파일 경로. 이 파일을 읽어 현재 어느 포트가 서비스 중인지 확인합니다.
NGINX_CONF="/etc/nginx/conf.d/service-url.inc"

# 2. 현재 실행 중인 포트 확인
CURRENT_PORT=$(cat ${NGINX_CONF} | grep -Po '[0-9]+' | tail -1)

echo ">>> Current service port: ${CURRENT_PORT}"

# 3. 새로운 버전(Green)을 띄울 포트 결정
if [ ${CURRENT_PORT} -eq ${BLUE_PORT} ]; then
  TARGET_PORT=${GREEN_PORT}
  OLD_PORT=${BLUE_PORT}
  echo ">>> New service port: ${TARGET_PORT}"
else
  TARGET_PORT=${BLUE_PORT}
  OLD_PORT=${GREEN_PORT}
  echo ">>> New service port: ${TARGET_PORT}"
fi

# 4. 새로운 버전(Green)의 Docker 이미지 다운로드 및 컨테이너 실행
# docker-compose.app.yml 파일을 이용해 새 컨테이너를 띄웁니다.
# IMAGE_TAG와 DOKCER_DB_PASSWORD는 GitHub Actions에서 export한 환경 변수입니다.
docker-compose -p wishpool-app-${TARGET_PORT} -f docker-compose.app.yml pull
docker-compose -p wishpool-app-${TARGET_PORT} -f docker-compose.app.yml up -d

# 5. 헬스 체크
echo ">>> Health check started on port ${TARGET_PORT}..."
for i in {1..10}; do
  # Spring Boot Actuator의 health 엔드포인트를 사용합니다.
  response_code=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:${TARGET_PORT}/actuator/health)

  if [ ${response_code} -eq 200 ]; then
    echo ">>> Health check successful!"

    # 6. Nginx 트래픽 전환
    echo "set \$service_url http://127.0.0.1:${TARGET_PORT};" | sudo tee ${NGINX_CONF}
    sudo nginx -s reload
    echo ">>> Traffic switched to port ${TARGET_PORT}."

    # 7. 기존 버전(Old) 컨테이너 종료
    docker-compose -p wishpool-app-${OLD_PORT} -f docker-compose.app.yml down
    echo ">>> Old container on port ${OLD_PORT} stopped."

    exit 0 # 배포 성공 후 종료
  fi

  echo ">>> Health check failed. Retrying in 5 seconds... (${i}/10)"
  sleep 5
done

# 8. 헬스 체크 최종 실패 시
echo ">>> Deployment failed."
# 실패한 새 버전 컨테이너 종료
docker-compose -p wishpool-app-${TARGET_PORT} -f docker-compose.app.yml down
exit 1 # 배포 실패 후 종료