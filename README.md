# mypoly-backend

## 한눈 요약
- Spring Boot 백엔드 (Gradle, Java 17)
- DB: PostgreSQL

## 환경
- Java: 17
- 빌드: Gradle
- Spring Boot: 3.1.6
- DB: PostgreSQL

## 핵심 의존성
- spring-boot-starter-web
- spring-boot-starter-validation
- spring-boot-starter-security
- spring-boot-starter-data-jpa
- postgresql
- flyway-core
- lombok
- spring-boot-starter-actuator
- springdoc-openapi-starter-webmvc-ui (권장)

## 빠른 시작
```bash
# 빌드
./gradlew clean build

# 개발 실행
./gradlew bootRun

# 배포용 JAR 실행
java -jar build/libs/mypoly-backend-0.0.1-SNAPSHOT.jar

# 테스트
./gradlew test
```
