# -------------------------------------------------------------------------------------
# Stage 1: Build Stage (Gradle을 사용하여 애플리케이션 빌드)
# -------------------------------------------------------------------------------------
# 빌드 시에만 필요한 Gradle + JDK 17 환경 (Alpine 리눅스 기반으로 가볍습니다)
FROM gradle:8.5.0-jdk17-alpine AS build

# 컨테이너 내 작업 디렉토리 설정
WORKDIR /app

# 1. 빌드 속도 향상을 위해 Gradle 설정 파일부터 복사 (캐시 활용)
COPY gradlew .
COPY gradle gradle
COPY build.gradle settings.gradle ./

# 2. 의존성 미리 다운로드 (소스 코드가 바뀌어도 의존성이 안 바뀌면 이 단계는 건너뜁니다)
RUN ./gradlew dependencies --no-daemon

# 3. 전체 소스 코드 복사
COPY src src

# 4. Spring Boot 실행 가능한 JAR 생성 (테스트는 생략하여 빌드 속도 단축)
RUN ./gradlew bootJar -x test --no-daemon

# -------------------------------------------------------------------------------------
# Stage 2: Runtime Stage (실제 서버에서 돌아갈 가벼운 실행 환경)
# -------------------------------------------------------------------------------------
# 실행 시에는 JDK가 아닌 JRE만 포함된 경량 이미지를 사용합니다.
FROM eclipse-temurin:17-jre-alpine

# 컨테이너 내 작업 디렉토리
WORKDIR /app

# 빌드 스테이지에서 생성된 JAR 파일만 쏙 빼서 현재 컨테이너로 복사
COPY --from=build /app/build/libs/*.jar app.jar

# (중요) 업로드된 파일이 저장될 디렉토리 생성 (carelink 설정 기준)
RUN mkdir -p /app/uploads/prescriptions

# Spring Boot 기본 포트 노출
EXPOSE 8080

# 컨테이너 시작 시 실행될 명령어
# -Djava.security.egd 옵션은 서버 부팅 속도를 올려줍니다.
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]