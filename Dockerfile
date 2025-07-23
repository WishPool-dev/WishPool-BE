# 실행을 위한 가벼운 JRE 이미지
FROM openjdk:17-slim

# 작업 디렉토리 설정
WORKDIR /app

# GitHub Actions Runner에서 빌드된 JAR 파일을 복사
# build/libs/ 디렉토리 안에 하나의 jar 파일만 있다고 가정
COPY *.jar app.jar

# 컨테이너 실행
ENTRYPOINT ["java","-jar","/app/app.jar"]