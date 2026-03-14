# user-service

`user-service`는 MyPoly의 사용자 인증과 사용자 기본 정보를 담당하는 서비스입니다.

현재 저장소 기준으로 이 서비스는 **소셜 SDK 토큰 검증 기반 로그인/회원가입**, **JWT / Refresh Token 발급**, **사용자 프로필/온보딩/약관/주소/닉네임 API**를 제공합니다.

## 주요 책임

- 소셜 토큰 검증
  - Kakao `ACCESS_TOKEN`
  - Google `ID_TOKEN`
  - Apple `ID_TOKEN`
- 회원가입 / 로그인 처리
- Access Token(JWT) / Refresh Token 발급
- refresh token 기반 access token 재발급
- 사용자 프로필 조회/수정
- 닉네임 중복 확인 / 랜덤 닉네임 생성
- 주소 검색
- 약관 목록 / 약관 상세 / 약관 HTML 조회
- 온보딩 상태 조회 / 변경
- 다른 서비스용 내부 세션 검증 API 제공

## 사용 기술

- Spring Boot 3.1.6
- Spring Web
- Spring Data JPA
- Spring Validation
- Spring Actuator
- Spring OAuth2 Resource Server (`NimbusJwtDecoder` 기반 ID Token 검증)
- Flyway
- QueryDSL
- springdoc-openapi

## 실행 프로필

- 기본 활성 profile: `local`
- 설정 파일
  - `src/main/resources/application.properties`
  - `src/main/resources/application-local.properties`
  - `src/main/resources/application-prod.properties`

### profile별 특징

#### local
- Swagger 활성화
- `social.dev-auth.enabled=true`
- `user.dev-delete.enabled=true`
- `DEV_AUTH_KEY` 미주입 시 기본값 `local-dev-key`

#### prod
- Swagger 비활성화
- SQL 로그 최소화

## API 경로 요약

로컬 기본값은 context path가 없으므로 아래 경로를 그대로 사용합니다.

Kubernetes에서는 Deployment에서 `SERVER_SERVLET_CONTEXT_PATH=/users`를 주입하므로 외부 경로는 `/users` prefix가 붙습니다.

예)
- 로컬: `/auth/token/google/login`
- K8s: `/users/auth/token/google/login`

## 공개 엔드포인트

보안 설정(`SecurityEndpoints.PUBLIC_ENDPOINTS`) 기준 공개 API는 아래와 같습니다.

- `POST /auth/**`
- `GET /terms/**`
- `GET /nicknames/availability`
- `GET /nicknames/random`
- Swagger 관련 경로
- Actuator health 관련 경로

즉, 로그인 이후 사용자 JWT가 필요한 API를 제외하면 위 경로들은 인증 없이 접근 가능합니다.

## 인증 API

컨트롤러 기준 base path는 `/auth`입니다.

### 1) 소셜 토큰 로그인
- `POST /auth/token/{provider}/login`

지원 provider / tokenType 조합은 현재 코드 기준으로 아래와 같습니다.

- `kakao` + `ACCESS_TOKEN`
- `google` + `ID_TOKEN`
- `apple` + `ID_TOKEN`

성공 시 응답에는 아래 정보가 포함됩니다.
- `userId`
- `provider`
- `providerUserId`
- `nickname`
- `profileImageUrl`
- `jwt`
- `refreshToken`

### 2) 소셜 토큰 회원가입
- `POST /auth/token/{provider}/signup`

회원가입 시에는 아래 처리가 함께 일어납니다.
- 소셜 토큰 검증
- 닉네임 유효성 / 중복 검증
- 사용자 생성 (`users`, `user_oauths`)
- 약관 동의 저장 (`user_terms`)
- 온보딩 상태를 `SIGNUP`으로 변경
- 새 `sid` 발급 후 access / refresh token 발급

### 3) access token 재발급
- `POST /auth/refresh`

요청 본문
- `refreshToken`

현재 동작 정책은 아래와 같습니다.
- refresh token 서명 검증
- refresh token 내부 `userId`, `sid` 추출
- 현재 DB의 `auth_session_id`와 비교하여 세션 유효성 확인
- 유효하면 **새 access token만 발급**
- refresh token은 재발급하지 않음
- refresh 시 `sid`도 회전하지 않음

## Dev 전용 인증 API

### 1) Dev 로그인
- `POST /dev-auth/login`
- 조건: `social.dev-auth.enabled=true`
- 헤더: `X-DEV-KEY`

local에서는 Swagger 테스트용으로 기본 활성화됩니다.

성공 시 일반 로그인과 동일하게 아래를 발급합니다.
- `jwt`
- `refreshToken`

### 2) Dev 사용자 삭제
- `DELETE /dev-users/me`
- 조건: `user.dev-delete.enabled=true`
- JWT 필요

## 사용자 API

### 내 정보 / 프로필
- `GET /me`
- `PATCH /me/profile`

### 닉네임
- `GET /nicknames/availability`
- `GET /nicknames/random`

### 주소
- `GET /addresses`

### 온보딩 상태
- `PATCH /{userId}/onboarding-status`
- `GET /{userId}/onboarding-status`

> 온보딩 상태 변경은 로그인 사용자와 path의 `userId`가 같을 때만 허용됩니다.

## 약관 API

- `GET /terms`
- `GET /terms/{termsId}`
- `GET /terms/{termsId}/html`

약관 API는 로그인 전에도 접근 가능합니다.

## 내부 API

다른 서비스가 현재 access token의 세션 유효성을 검증할 때 사용하는 내부 API입니다.

- `GET /internal/auth/session/validate`
- query parameter
  - `userId`
  - `sid`
- Authorization 헤더 필요

이 API는 bill-service가 JWT의 `sid`가 현재 유효한 세션인지 확인할 때 사용합니다.

## 인증 / 세션 정책

### 1) 단일 세션 정책
로그인 또는 회원가입 성공 시 `AuthSessionService.issueNewSession(userId)`가 호출되어 새 `sid`가 발급됩니다.

그 결과,
- 같은 계정이 새 기기에서 다시 로그인하면
- 이전 기기의 access token / refresh token은 더 이상 유효하지 않게 됩니다.

### 2) user-service 내부 검증 방식
`user-service`는 DB의 `users.auth_session_id`와 JWT 안의 `sid`를 직접 비교합니다.

### 3) refresh token
- 별도 secret(`REFRESH_JWT_SECRET`) 사용
- claim `typ=refresh` 포함
- claim `sid` 포함

## 필요한 환경변수

### 필수
- `SERVER_PORT`
- `SPRING_PROFILES_ACTIVE`
- `DB_URL`
- `DB_USERNAME`
- `DB_PASSWORD`
- `JWT_SECRET`
- `REFRESH_JWT_SECRET`

### 소셜 로그인 관련
- `KAKAO_CLIENT_ID`
- `KAKAO_CLIENT_SECRET`
- `KAKAO_APP_ID` (선택)
- `GOOGLE_ALLOWED_AUDIENCES` 또는 `GOOGLE_CLIENT_ID`
- `APPLE_ALLOWED_AUDIENCES` 또는 `APPLE_CLIENT_ID`

### 선택
- `DEV_AUTH_KEY`
- `SERVER_FORWARD_HEADERS_STRATEGY`
- `SERVER_SERVLET_CONTEXT_PATH`
- `JWT_EXPIRATION_MILLIS`
- `REFRESH_JWT_EXPIRATION_MILLIS`

## Swagger

Swagger는 기본 설정이 아니라 **local profile에서만 활성화**됩니다.

### 로컬
- `http://localhost:8080/swagger-ui.html`
- `http://localhost:8080/v3/api-docs`

### Kubernetes
- `https://api-mypoly.kro.kr/users/swagger-ui.html`
- `https://api-mypoly.kro.kr/users/v3/api-docs`

> 단, 현재 K8s ConfigMap이 `SPRING_PROFILES_ACTIVE=local`일 때만 활성 상태가 유지됩니다.

## 데이터베이스 스키마

사용 스키마: `user_service`

### 주요 테이블
- `users`
- `terms`
- `user_oauths`
- `user_terms`
- `nickname_forbidden_words`
- `nickname_words`
- `administrative_area`

### 세션 관련 컬럼
- `users.auth_session_id`
  - 마이그레이션: `V6__add_auth_session_id.sql`

## Flyway 마이그레이션

- `V1__init.sql`
- `V2__seed_terms.sql`
- `V3__seed_nickname_forbidden_words.sql`
- `V4__seed_nickname_random_words.sql`
- `V5__seed_administrative_area.sql`
- `V6__add_auth_session_id.sql`

## 로컬 실행 예시

### Docker Compose
루트에서 실행

```bash
docker compose up -d db user-service --build
```

### 단독 bootRun 예시
루트에서 실행

```bash
SPRING_PROFILES_ACTIVE=local \
SERVER_PORT=8080 \
DB_URL=jdbc:postgresql://localhost:5432/mypoly \
DB_USERNAME=mypoly \
DB_PASSWORD=your_password \
JWT_SECRET=your_access_secret \
REFRESH_JWT_SECRET=your_refresh_secret \
KAKAO_CLIENT_ID=your_kakao_client_id \
KAKAO_CLIENT_SECRET=your_kakao_client_secret \
./gradlew :user-service:bootRun
```

## 참고 사항

- 예전 OAuth2 redirect 기반 문서와 달리, 현재 구현의 공식 로그인 진입점은 **소셜 SDK 토큰을 받아 처리하는 POST API**입니다.
- 코드 내부 OpenAPI 설명문 일부에는 과거 문구가 남아 있을 수 있으나, 실제 동작 기준은 컨트롤러와 보안 설정을 따릅니다.
