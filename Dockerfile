# 1. 실행에 최적화된 경량 JRE 환경 사용
FROM eclipse-temurin:17-jre-alpine

# 2. 작업 디렉토리 설정
WORKDIR /app

# 3. Actions에서 빌드 완료된 JAR 파일을 컨테이너 안으로 복사
# (빌드된 파일 이름이 유동적일 수 있으므로 *.jar 사용)
COPY build/libs/*.jar app.jar

# 4. 업로드 디렉토리 생성 (carelink 설정 기준)
RUN mkdir -p /app/uploads/prescriptions

# 5. 실행 포트
EXPOSE 8080

# 6. 부팅 속도 최적화 옵션과 함께 실행
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]