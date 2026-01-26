docker-compose up -d
# mypoly-user-service (소셜 로그인 백엔드)

## 주요 기능
- 카카오 소셜 로그인 및 회원가입 (구글/애플 등 확장 가능)
- 최초 로그인 시 닉네임 null 저장, 이후 별도 입력
- 중복 회원가입 방지, 트랜잭션 롤백 보장
- 모든 주요 이벤트/예외 상황 로그 기록
- 표준 JSON 응답 및 명확한 에러 메시지

## API 예시
### 성공
```
{
  "success": true,
  "message": "소셜 로그인 성공",
  "data": {
    "provider": "kakao",
    "providerUserId": "4717271726",
    "nickname": null
  }
}
```
### 에러
```
{
  "success": false,
  "message": "필수값 누락 (providerUserId)",
  "data": null
}
```

## 보안/운영
- 인증되지 않은 접근은 Spring Security에서 자동 차단
- 모든 주요 이벤트 log.info/log.error 기록 (Docker 로그에서 확인)
- JVM/DB 타임존 Asia/Seoul(KST) 통일

## 배포/실행
1. 코드 수정 후 반드시 아래 순서로 배포
   - `./gradlew clean build`
   - `docker-compose build`
   - `docker-compose up -d`
2. DB/로그/응답 포맷 등은 README와 코드 참고

## 개발 환경
- Java 17, Spring Boot, Gradle, PostgreSQL, Docker
- 환경변수: `.env`, `user-service/.env` 참고 (민감정보는 git에 포함 금지)

## 프론트엔드 연동
- 카카오 로그인 시작: `{도메인}/oauth2/authorization/kakao`
- 로그인 성공 콜백: `/login/success` (JSON 반환)

## 협업/운영 주의사항
- .env 등 민감정보는 git에 올리지 말 것
- 카카오 OAuth2 Client ID/Secret, DB 비밀번호 등은 운영값으로 교체 필요
- 회원가입/연동, JWT 발급 등은 추가 구현 필요

## 문의
- 실무 환경/배포/연동/프론트엔드 API 등 문의는 담당자에게 직접 연락
