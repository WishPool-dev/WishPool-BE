# JDK 17 + wishpool 별칭 생성
FROM openjdk:17-jdk-slim AS wishpool

# 작업 디렉토리 /app
WORKDIR /workspace/app

# 빌드 속도 향상을 위한 의존성 캐싱
COPY build.gradle settings.gradle ./
COPY gradle ./gradle

# 의존성 복사
RUN ./gradlew dependencies

# 나머지 소스 코드를 복사 및 빌드
COPY src ./src
RUN ./gradlew build -x test

# 경량화 이미지 실행
FROM openjdk:17-slim

# 작업 디렉토리를 설정
WORKDIR /app

# wishpool에서 빌드된 jar 복사
COPY --from=wishpool /workspace/app/build/libs/*.jar app.jar

# 컨테이너 시작할 때 하위 명령어 실행
ENTRYPOINT ["java", "-jar", "app.jar"]