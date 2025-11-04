# 실행을 위한 가벼운 JRE 이미지
FROM eclipse-temurin:17-jdk-jammy

# 작업 디렉토리 설정
WORKDIR /app

# GitHub Actions Runner에서 빌드된 JAR 파일을 복사
# build/libs/ 디렉토리 안에 하나의 jar 파일만 있다고 가정
COPY build/libs/*.jar app.jar

# 컨테이너 실행
ENTRYPOINT ["java","-jar","/app/app.jar"]