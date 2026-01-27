# user-service (소셜 로그인/JWT)

`user-service`는 MyPoly의 사용자 인증/인가(소셜 로그인, JWT 발급) 책임을 담당합니다.

## 주요 기능
- Kakao OAuth2 로그인/회원가입
- 성공 시 JWT 발급 및 표준 JSON 응답(`ApiResponse`)
- Controller API DTO ↔ Application(Command/Result) DTO 분리
- Provider switch 제거(Provider 구현체 + Resolver 라우팅)
- K8s 운영 시 **context-path(`/users`)** 기반으로 Ingress rewrite 없이 동작

## OAuth2 인증 플로우 (개념)
1. 클라이언트가 로그인 시작 URL로 이동  
   - 로컬: `/oauth2/authorization/kakao`  
   - K8s: `/users/oauth2/authorization/kakao` (context-path 적용)
2. Kakao 인증 완료 후 Spring Security가 콜백을 처리  
   - redirect-uri: `{baseUrl}/login/oauth2/code/{registrationId}`
3. 성공 시 서버가 사용자 생성/조회 후 JWT 발급 → JSON 응답

> 콜백 URL(`/login/oauth2/code/...`)은 1회성 code를 사용하기 때문에, 새로고침(F5) 시 실패 응답이 나올 수 있습니다.

## 주요 엔드포인트
- 로그인 안내: `GET /login`
  - `data`에 OAuth2 시작 URL을 내려줍니다.
- OAuth2 로그인 시작: `GET /oauth2/authorization/kakao`
- (OAuth2 callback): `GET /login/oauth2/code/kakao`

> K8s에서 `SERVER_SERVLET_CONTEXT_PATH=/users`이면 위 경로 앞에 `/users`가 자동으로 붙습니다.

## 응답 포맷
모든 응답은 `ApiResponse<T>` 형태를 사용합니다.

성공 예시(형태 예시):
```json
{
  "success": true,
  "message": "카카오 소셜 로그인 성공",
  "data": {
    "userId": 1,
    "provider": "kakao",
    "providerUserId": "4717271726",
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

## 로컬 실행 (docker compose)
루트 README의 로컬 실행 가이드를 따릅니다.
- 기본: `http://localhost:8080`

## 환경변수
`src/main/resources/application.properties` 기준으로 아래 환경변수가 필요합니다.

- Kakao OAuth2
  - `KAKAO_CLIENT_ID`
  - `KAKAO_CLIENT_SECRET`
- JWT
  - `JWT_SECRET`
- DB
  - `DB_URL`
  - `DB_USERNAME`
  - `DB_PASSWORD`
- 서버
  - `SERVER_PORT` (컨테이너 내부는 보통 8080 고정)
  - `SERVER_SERVLET_CONTEXT_PATH` (K8s에서 `/users` 사용)

로컬 개발은 `.env.example` / `user-service/.env.example` 템플릿을 사용합니다.

## DTO/레이어 구조(요약)
- `api/*` : 컨트롤러, API 요청/응답 DTO
- `application/*` : 유스케이스(서비스), Command/Result DTO
- `domain/*` : 엔티티/도메인 모델
- `infrastructure/*` : repository, 외부 연동 등
