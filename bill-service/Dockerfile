FROM gradle:8.5-jdk17 AS builder
WORKDIR /workspace

COPY gradlew gradlew.bat settings.gradle build.gradle ./
COPY gradle ./gradle

# 멀티모듈: 공통 모듈/다른 서비스 모듈도 settings.gradle에 포함돼 있으니 같이 복사
COPY mypoly-security/build.gradle mypoly-security/build.gradle
COPY mypoly-security/src mypoly-security/src

# bill-service
COPY bill-service/build.gradle bill-service/build.gradle
COPY bill-service/src bill-service/src

# settings.gradle에 포함돼 있으니 "존재만" 시켜줌 (src는 필요 없음)
COPY user-service/build.gradle user-service/build.gradle

# bill-service만 bootJar 생성
RUN sed -i 's/\r$//' gradlew && chmod +x gradlew
RUN ./gradlew :bill-service:bootJar -x test --no-daemon

FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

COPY --from=builder /workspace/bill-service/build/libs/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
