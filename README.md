# mypoly-backend

`mypoly-backend`는 MyPoly 서비스의 백엔드 모노레포입니다. Gradle 멀티모듈 구조로 `user-service`, `bill-service`, 공통 모듈(`mypoly-common`, `mypoly-security`)을 함께 관리합니다.

이 README는 **현재 저장소 기준의 실제 구조와 실행 방법**만 정리합니다. 서비스 상세는 각 서비스 README를 참고하세요.

## 모듈 구성

- `user-service`
  - 소셜 토큰 검증 기반 로그인/회원가입
  - JWT / Refresh Token 발급
  - 사용자 정보, 주소 검색, 닉네임, 약관, 온보딩 상태 관리
- `bill-service`
  - 의안 카테고리 조회
  - 사용자 관심 의안 카테고리 저장
  - 온보딩 과정의 관심 의안 카테고리 저장 후 `user-service`의 온보딩 상태 갱신
- `mypoly-common`
  - 공통 DTO, 예외, 보안 핸들러 등 공통 코드
- `mypoly-security`
  - JWT 유틸, JWT 인증 필터, 세션 검증 인터페이스 등 공통 보안 코드

## 저장소 구조

```text
mypoly-backend/
├── bill-service/
├── user-service/
├── mypoly-common/
├── mypoly-security/
├── k8s/
│   └── base/
│       ├── infra/
│       │   ├── jwt/
│       │   └── postgres/
│       └── services/
│           ├── bill-service/
│           └── user-service/
├── docker-compose.yml
├── README.md
└── README-k8s.md
```

## 기술 스택

- Java 17
- Spring Boot 3.1.6
- Gradle 멀티모듈
- PostgreSQL 15
- Spring Security
- Spring Data JPA + Flyway
- QueryDSL
- springdoc-openapi
- Docker / Docker Compose
- Kubernetes + Kustomize
- cert-manager + Let’s Encrypt

## 현재 로컬 실행 방식

저장소에는 `docker-compose.yml`이 포함되어 있고, 로컬에서는 보통 아래 3개 컨테이너 조합으로 실행합니다.

- `db` → PostgreSQL
- `user-service` → 호스트 기본 포트 `8080`
- `bill-service` → 호스트 기본 포트 `8081`

### 1) 환경변수 파일 준비

```bash
cp .env.example .env
cp user-service/.env.example user-service/.env
cp bill-service/.env.example bill-service/.env
```

### 2) 필요한 환경변수 확인

#### 공통
- `.env`
  - `POSTGRES_DB`
  - `POSTGRES_USER`
  - `POSTGRES_PASSWORD`
  - `POSTGRES_PORT`

#### user-service
- `user-service/.env`
  - `SPRING_PROFILES_ACTIVE`
  - `USER_SERVICE_PORT`
  - `KAKAO_CLIENT_ID`
  - `KAKAO_CLIENT_SECRET`
  - `JWT_SECRET`
  - `REFRESH_JWT_SECRET`
  - `DEV_AUTH_KEY` (선택, local 기본값 사용 가능)
  - `SERVER_FORWARD_HEADERS_STRATEGY` (선택)
  - `SERVER_SERVLET_CONTEXT_PATH` (선택, 로컬 기본값은 빈 값)
  - `GOOGLE_ALLOWED_AUDIENCES` 또는 `GOOGLE_CLIENT_ID` (Google 로그인 테스트 시)
  - `APPLE_ALLOWED_AUDIENCES` 또는 `APPLE_CLIENT_ID` (Apple 로그인 테스트 시)
  - `KAKAO_APP_ID` (Kakao app_id 강제 검증이 필요할 때만)

#### bill-service
- `bill-service/.env`
  - `SPRING_PROFILES_ACTIVE`
  - `BILL_SERVICE_PORT`
  - `JWT_SECRET`
  - `SERVER_FORWARD_HEADERS_STRATEGY` (선택)
  - `BILL_SERVER_SERVLET_CONTEXT_PATH` (선택, 로컬 기본값은 빈 값)
  - `USER_SERVICE_URL` (선택, 기본값 `http://user-service:80/users/`)
  - `USER_SERVICE_INTERNAL_URL` (선택, 기본값 `http://user-service:80/users`)
  - `BILL_CATEGORY_ICON_BASE_URL` (선택, 기본값 `https://storage.googleapis.com/mypoly-assets-dev`)

> `user-service/.env.example`, `bill-service/.env.example`에는 일부 최신 필수 키가 아직 반영되지 않았을 수 있습니다. 실행 기준의 실제 필수값은 각 서비스의 `application.properties`와 공통 보안 모듈(`mypoly-security`) 기준으로 위 목록을 따라가면 됩니다.

### 3) 컨테이너 실행

```bash
docker compose up -d db user-service bill-service --build
```

### 4) 로컬 접속 경로

- user-service
  - API base: `http://localhost:8080`
  - Swagger UI: `http://localhost:8080/swagger-ui.html`
- bill-service
  - API base: `http://localhost:8081`
  - Swagger UI: `http://localhost:8081/swagger-ui.html`

로컬 기본값은 context path가 비어 있습니다. 즉, 로컬에서는 `/users`, `/bills` prefix 없이 바로 접근합니다.

## 서비스별 참고 문서

- `user-service/README.md`
- `bill-service/README.md`
- `README-k8s.md`

## 데이터베이스 / 스키마 구성

서비스별로 PostgreSQL 스키마를 분리해 사용합니다.

- `user-service` → `user_service`
- `bill-service` → `bill_service`

각 서비스는 Flyway로 자기 스키마를 관리합니다.

### user-service 주요 마이그레이션
- `V1__init.sql`
- `V2__seed_terms.sql`
- `V3__seed_nickname_forbidden_words.sql`
- `V4__seed_nickname_random_words.sql`
- `V5__seed_administrative_area.sql`
- `V6__add_auth_session_id.sql`

### bill-service 주요 마이그레이션
- `V1__init.sql`
- `V2__seed_bill_categories.sql`

## 인증 구조 요약

### 1) 토큰 발급 책임
- JWT / Refresh Token 발급은 `user-service`가 담당합니다.
- `bill-service`는 토큰을 발급하지 않고, `user-service`가 발급한 JWT를 검증만 합니다.

### 2) 단일 세션 정책
- `user-service`는 로그인 또는 회원가입 성공 시 새 `sid(auth_session_id)`를 발급합니다.
- 사용자가 다시 로그인하면 이전 기기의 세션은 무효화됩니다.
- `bill-service`는 요청마다 `user-service` 내부 API(`/internal/auth/session/validate`)를 호출해 현재 `sid`가 유효한지 확인합니다.

### 3) refresh 정책
- `POST /auth/refresh`는 **새 access token만 재발급**합니다.
- 현재 구현은 refresh 시 `sid`를 회전시키지 않으며, refresh token 자체를 다시 발급하지도 않습니다.

## Swagger / OpenAPI

두 서비스 모두 springdoc-openapi를 사용합니다.

- 기본 `application.properties`에서는 Swagger가 비활성화되어 있습니다.
- `application-local.properties`에서만 활성화됩니다.
- 따라서 로컬 profile(`SPRING_PROFILES_ACTIVE=local`)로 실행할 때 Swagger UI를 사용할 수 있습니다.

## 로컬 개발 시 참고

- 테스트 코드는 현재 저장소에 포함되어 있지 않습니다.
- Dockerfile은 각 서비스별 멀티스테이지 빌드 방식입니다.
- 두 서비스 모두 `bootJar` 기준으로 이미지를 생성합니다.

## Kubernetes 배포 문서

Kubernetes 관련 내용은 루트 README가 아니라 `README-k8s.md`에서 관리합니다.

현재 저장소에는 **`k8s/base`만 존재**하며, 예전 문서에 있던 `k8s/overlays/*` 구조는 현재 저장소 기준으로는 없습니다.
