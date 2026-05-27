FROM gradle:8.5-jdk17 AS builder
WORKDIR /workspace

COPY gradlew gradlew.bat settings.gradle build.gradle ./
COPY gradle ./gradle

# 멀티모듈: settings.gradle 에 포함된 다른 모듈도 같이 복사
COPY mypoly-common/build.gradle mypoly-common/build.gradle
COPY mypoly-common/src mypoly-common/src

COPY mypoly-security/build.gradle mypoly-security/build.gradle
COPY mypoly-security/src mypoly-security/src

# notification-service 모듈
COPY notification-service/build.gradle notification-service/build.gradle
COPY notification-service/src notification-service/src

# settings.gradle 에 포함돼 있으니 build.gradle 만 존재시킴
COPY user-service/build.gradle user-service/build.gradle
COPY bill-service/build.gradle bill-service/build.gradle

RUN sed -i 's/\r$//' gradlew && chmod +x gradlew
RUN ./gradlew :notification-service:bootJar -x test --no-daemon

FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
ENV TZ=Asia/Seoul

COPY --from=builder /workspace/notification-service/build/libs/*.jar app.jar

USER 10001

EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
