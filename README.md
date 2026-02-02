# mypoly-backend

MyPoly 서비스의 백엔드 모노레포(Gradle 멀티모듈)입니다.  
현재는 `user-service`(소셜 로그인/JWT)부터 구현을 진행하고 있으며, 향후 도메인/서비스를 추가로 분리해 확장하는 구조를 전제로 합니다.

## 최근 변경점 (이전 README 대비)
- **Swagger(OpenAPI) 문서화 적용**: `springdoc-openapi` 기반으로 Swagger UI 제공
- **OAuth2 로그인 시작 URL 정리**: `GET /auth/{provider}` 엔드포인트 추가(302 리다이렉트)
  - 프론트/클라이언트는 Spring Security 내부 엔드포인트(`/oauth2/authorization/{provider}`) 대신 **`/auth/{provider}`를 공식 진입점**으로 사용
- **Kubernetes HTTPS 적용**: `cert-manager` + Let’s Encrypt(LE)로 TLS 발급/적용
  - Ingress에서 HTTP→HTTPS 강제 리다이렉트
  - LE 인증서는 cert-manager가 만료 전에 자동 갱신

> 변경 상세/운영 가이드는 `README-k8s.md` / `user-service/README.md`를 참고하세요.

---

## 현재 구현 범위
- `user-service`
  - Kakao OAuth2 로그인/회원가입
  - JWT 발급(성공 시) + 표준 JSON 응답(`ApiResponse`)
  - Controller API DTO ↔ Application(Command/Result) DTO 분리
  - Provider switch 제거(Provider 별 구현체 + Resolver 라우팅)
  - K8s 운영 시 **context-path(`/users`)** 로 운영 (Ingress rewrite 없이 `/users/**`로 동작)
  - **Swagger(OpenAPI) 문서 제공**

## 문서
- 서비스 상세: `user-service/README.md`
- Kubernetes 배포/HTTPS/인증서: `README-k8s.md`

## 기술 스택
- Java 17
- Spring Boot **3.1.6**
- Gradle (멀티모듈)
- PostgreSQL
- Spring Security (OAuth2 Client)
- springdoc-openapi (Swagger UI)
- Docker / docker compose
- Kubernetes (Kustomize overlay)

## 디렉터리 구조
- `user-service/` : 소셜 로그인/JWT 서비스
- `k8s/` : Kustomize 기반 배포 리소스(base + overlays)
- `README-k8s.md` : K8s 배포/인증서/HTTPS 가이드
- `.env.example`, `user-service/.env.example` : 로컬 실행용 환경변수 템플릿

---

## 로컬 실행 (docker compose)

### 1) 환경변수 준비
```bash
cp .env.example .env
cp user-service/.env.example user-service/.env
```

### 2) 빌드/기동
```bash
docker compose build user-service
docker compose up -d db user-service
```

### 3) 동작 확인
#### API(로그인 시작)
- **공식 로그인 시작 URL**: `GET http://localhost:8080/auth/kakao`
  - 응답: 302(FOUND) → `Location: /oauth2/authorization/kakao`

> `provider`는 Spring Security registration id(예: `kakao`)와 동일해야 합니다.

#### Swagger
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

> 로컬에서 `SERVER_SERVLET_CONTEXT_PATH=/users`로 띄우면 위 URL 앞에 `/users`가 붙습니다.

---

## 환경변수 관리 원칙
- 공통 인프라(예: PostgreSQL 계정/DB명)는 루트 `.env`에 둡니다.
- 서비스 전용 시크릿(예: Kakao Client Secret, JWT_SECRET)은 `user-service/.env`에 둡니다.
- 실제 운영 시에는 `.env`를 쓰기보다 K8s `Secret/ConfigMap` 또는 별도 시크릿 관리(ExternalSecrets 등)를 권장합니다.

## Swagger 운영 팁
- 기본은 개발 편의를 위해 Swagger UI가 켜져 있습니다.
- 운영에서 끄고 싶으면 환경변수로 제어할 수 있습니다.
  - `OPENAPI_ENABLED=false`
  - `SWAGGER_UI_ENABLED=false`

## 멀티 서비스로 확장 시 Swagger는 어떻게?
현재는 **서비스별로 Swagger를 제공**합니다(`user-service` 단위).  
서비스가 늘어나면 선택지는 크게 2가지입니다.

1) **서비스별 Swagger 유지(추천: 초기)**
- 각 서비스가 자기 스펙을 책임지고 배포/버전 관리도 쉬움

2) **루트에서 통합 문서 제공(추후)**
- API Gateway/Docs 포털을 두고 각 서비스의 OpenAPI를 모아서 노출
- 예: Spring Cloud Gateway + Swagger Aggregator, 또는 별도 Docs 서비스

> 지금 단계에선 1)로 가고, 서비스가 늘어나고 외부 공유가 필요해질 때 2)로 전환하는 흐름을 추천합니다.

---

## 개발 메모
- 모노레포 루트 README는 “프로젝트 홈/개요” 역할만 담당하고,
  서비스 내부 동작/엔드포인트는 서비스 디렉터리(`user-service/README.md`)에서 관리합니다.
- 빌드 산출물(`build/`, `bin/`)은 커밋 대상이 아닙니다.
