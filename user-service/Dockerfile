FROM gradle:8.5-jdk17 AS builder
WORKDIR /workspace

COPY gradlew gradlew.bat settings.gradle build.gradle ./
COPY gradle ./gradle

# 멀티모듈: 공통 모듈/다른 서비스 모듈도 settings.gradle에 포함돼 있으니 같이 복사
COPY mypoly-common/build.gradle mypoly-common/build.gradle
COPY mypoly-common/src mypoly-common/src

COPY mypoly-security/build.gradle mypoly-security/build.gradle
COPY mypoly-security/src mypoly-security/src

# user-service 모듈 복사
COPY user-service/build.gradle user-service/build.gradle
COPY user-service/src user-service/src

# settings.gradle에 포함돼 있으니 "존재만" 시켜줌 (src는 필요 없음)
COPY bill-service/build.gradle bill-service/build.gradle

# user-service만 bootJar 생성
RUN sed -i 's/\r$//' gradlew && chmod +x gradlew
RUN ./gradlew :user-service:bootJar -x test --no-daemon

FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
ENV TZ=Asia/Seoul

COPY --from=builder /workspace/user-service/build/libs/*.jar app.jar

USER 10001

EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
