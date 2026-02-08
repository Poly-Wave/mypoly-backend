FROM gradle:8.5-jdk17 AS builder
WORKDIR /workspace

COPY gradlew gradlew.bat settings.gradle build.gradle ./
COPY gradle ./gradle

# 멀티모듈: 공통 모듈도 같이 복사해야 빌드됨
COPY mypoly-security/build.gradle mypoly-security/build.gradle
COPY mypoly-security/src mypoly-security/src

COPY user-service/build.gradle user-service/build.gradle
COPY user-service/src user-service/src

# user-service만 bootJar 생성 (테스트는 필요 시 -x test 제거)
RUN sed -i 's/\r$//' gradlew && chmod +x gradlew
RUN ./gradlew :user-service:bootJar -x test --no-daemon

FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

COPY --from=builder /workspace/user-service/build/libs/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
