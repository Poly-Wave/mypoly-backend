# user-service (소셜 로그인/JWT)

`user-service`는 MyPoly의 사용자 인증/인가(소셜 로그인, JWT 발급) 책임을 담당합니다.

## 최근 변경점 (이전 README 대비)
- **Swagger(OpenAPI) 문서화 추가**
  - Swagger UI: `/swagger-ui.html`
  - OpenAPI JSON: `/v3/api-docs`
- **로그인 시작 URL을 `/auth/{provider}`로 통일**
  - `GET /auth/kakao` → 302로 `/oauth2/authorization/kakao` 리다이렉트
  - K8s context-path(`/users`) 환경에서도 리다이렉트 Location이 안전하게 생성

---

## 주요 기능
- Kakao OAuth2 로그인/회원가입
- 성공 시 JWT 발급 및 표준 JSON 응답(`ApiResponse`)
- Controller API DTO ↔ Application(Command/Result) DTO 분리
- Provider switch 제거(Provider 구현체 + Resolver 라우팅)
- K8s 운영 시 **context-path(`/users`)** 기반으로 Ingress rewrite 없이 동작
- Swagger(OpenAPI) 문서 제공

---

## 엔드포인트

### 1) OAuth2 로그인 시작(공식)
- `GET /auth/{provider}`
  - 예) `GET /auth/kakao`
  - 응답: **302 Found**
  - Header: `Location: /oauth2/authorization/{provider}`

> `provider` 값은 Spring Security `registrationId`와 동일해야 합니다.
> (현재는 `kakao`만 설정되어 있으므로 `google`, `apple`은 registration이 없다면 404가 떨어집니다.)

### 2) OAuth2 시작(내부: Spring Security가 처리)
- `GET /oauth2/authorization/{provider}`

### 3) OAuth2 콜백(내부: Spring Security가 처리)
- `GET /login/oauth2/code/{provider}`
  - 성공 시: JWT 포함 JSON 응답(`ApiResponse.ok`)
  - 실패 시: 401 + `ApiResponse.fail`

> 콜백 URL은 1회성 code를 사용하는 구조라서, 새로고침(F5)하면 실패 응답이 나올 수 있습니다.

---

## Swagger(OpenAPI)
- Swagger UI: `GET /swagger-ui.html`
- OpenAPI JSON: `GET /v3/api-docs`

### K8s context-path(`/users`) 환경에서는?
K8s에서 `SERVER_SERVLET_CONTEXT_PATH=/users`를 사용하므로 경로 앞에 `/users`가 붙습니다.
- Swagger UI: `/users/swagger-ui.html`
- OpenAPI JSON: `/users/v3/api-docs`
- OAuth 시작: `/users/auth/kakao`

### 운영에서 Swagger 끄기
`application.properties`에서 환경변수로 토글할 수 있습니다.
- `OPENAPI_ENABLED=false`
- `SWAGGER_UI_ENABLED=false`

---

## OAuth2 인증 플로우 (개념)
1. 클라이언트가 로그인 시작 URL로 이동
   - 로컬: `/auth/kakao`
   - K8s: `/users/auth/kakao`
2. `user-service`가 302로 Spring Security OAuth2 시작 엔드포인트로 리다이렉트
   - `/oauth2/authorization/kakao`
3. Kakao 인증 완료 후 Spring Security가 콜백(`/login/oauth2/code/kakao`)을 처리
4. 성공 시 서버가 사용자 생성/조회 후 JWT 발급 → JSON 응답

---

## 응답 포맷
모든 응답은 `ApiResponse<T>` 형태를 사용합니다.

성공 예시(형태 예시):
```json
{
  "success": true,
  "message": "소셜 로그인 성공",
  "data": {
    "userId": 1,
    "provider": "kakao",
    "providerUserId": "4717271726",
    "nickname": null,
    "profileImageUrl": null,
    "jwt": "..."
  }
}
```

실패 예시(형태 예시):
```json
{
  "success": false,
  "message": "소셜 로그인 실패",
  "data": null
}
```

---

## 로컬 실행 (docker compose)
루트 README의 로컬 실행 가이드를 따릅니다.
- 기본: `http://localhost:8080`
- Swagger: `http://localhost:8080/swagger-ui.html`

---

## 환경변수
`src/main/resources/application.properties` 기준으로 아래 환경변수가 필요합니다.

### Kakao OAuth2
- `KAKAO_CLIENT_ID`
- `KAKAO_CLIENT_SECRET`

### JWT
- `JWT_SECRET`

### DB
- `DB_URL`
- `DB_USERNAME`
- `DB_PASSWORD`

### 서버/프록시
- `SERVER_PORT` (컨테이너 내부는 보통 8080 고정)
- `SERVER_SERVLET_CONTEXT_PATH` (K8s에서 `/users` 사용)
- `SERVER_FORWARD_HEADERS_STRATEGY` (Ingress 뒤에서 https/host 인식용)

### Swagger 토글(옵션)
- `OPENAPI_ENABLED` (default: `true`)
- `SWAGGER_UI_ENABLED` (default: `true`)

로컬 개발은 `.env.example` / `user-service/.env.example` 템플릿을 사용합니다.

---

## DTO/레이어 구조(요약)
- `api/*` : 컨트롤러, API 요청/응답 DTO
- `application/*` : 유스케이스(서비스), Command/Result DTO
- `domain/*` : 엔티티/도메인 모델
- `infrastructure/*` : repository, 외부 연동 등

