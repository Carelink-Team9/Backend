# 1. 실행 환경을 Java 21로 변경 (프로젝트 빌드 버전과 일치)
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# 2. JAR 파일 복사
COPY build/libs/*.jar app.jar

# 3. 업로드 디렉토리 생성
RUN mkdir -p /app/uploads/prescriptions

# 4. 실행 포트 및 명령어
EXPOSE 8080
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]