
# mypoly-user-service (소셜 로그인 백엔드)

## 주요 기능
- 카카오 소셜 로그인 및 회원가입 (구글/애플 등 확장 가능 구조)
- JWT 발급 및 인증, 표준 JSON 응답
- 중복 회원가입 방지, 트랜잭션 롤백 보장
- 모든 주요 이벤트/예외 상황 로그 기록
- Spring Security 기반 인증/인가

## OAuth2 인증 플로우
1. 프론트엔드에서 `/oauth2/authorization/kakao`로 이동(redirect)
2. 카카오 인증 완료 → 백엔드 `/login/oauth2/code/kakao?code=...&state=...`로 자동 리다이렉트
3. 서버에서 JWT 및 사용자 정보(JSON) 반환

### 주요 엔드포인트
- **카카오 로그인 시작:** `GET /oauth2/authorization/kakao`
- **로그인 안내:** `GET /login` (안내 메시지 반환)
- **JWT 인증 필요 API:** (추후 확장)

### 성공 응답 예시
```
{
  "success": true,
  "message": "소셜 로그인 성공",
  "data": {
    "userId": 1,
    "provider": "kakao",
    "providerUserId": "4717271726",
    "nickname": null,
    "profileImageUrl": "...",
    "jwt": "..."
  }
}
```
### 에러 예시
```
{
  "success": false,
  "message": "필수값 누락 (providerUserId)",
  "data": null
}
```

## 환경 변수 및 설정
- `.env`, `user-service/.env`에 DB, 카카오 OAuth2, JWT_SECRET 등 필수 환경변수 설정
- 예시:
  - `KAKAO_CLIENT_ID=...`
  - `KAKAO_CLIENT_SECRET=...`
  - `JWT_SECRET=...` (32bytes 이상)
  - `SPRING_DATASOURCE_URL=...`

## 배포/실행
1. 코드 수정 후 반드시 아래 순서로 배포
   - `./gradlew clean build`
   - `docker-compose build`
   - `docker-compose up -d`
2. 로그/응답 포맷 등은 README와 코드 참고

## 개발 환경
- Java 17, Spring Boot 3.x, Gradle, PostgreSQL, Docker
- 환경변수: `.env`, `user-service/.env` 참고 (민감정보는 git에 포함 금지)

## 프론트엔드 연동 가이드
- 카카오 로그인 시작: `{도메인}:8081/oauth2/authorization/kakao`
- 인증 성공 시 서버에서 JWT 및 사용자 정보(JSON) 반환
- JWT는 이후 Authorization 헤더(`Bearer {jwt}`)로 사용

## 협업/운영 주의사항
- .env 등 민감정보는 git에 올리지 말 것
- 카카오 OAuth2 Client ID/Secret, DB 비밀번호 등은 운영값으로 교체 필요
- 회원가입/연동, JWT 재발급 등은 추가 구현 가능

## 문의
- 실무 환경/배포/연동/프론트엔드 API 등 문의는 담당자에게 직접 연락
