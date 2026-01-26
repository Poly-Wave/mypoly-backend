
# mypoly-backend

## 프로젝트 개요
- Spring Boot 기반 백엔드 (Java 17, Gradle)
- PostgreSQL DB 사용
- **카카오 OAuth2 로그인 인증** 기능 구현 (DB 연동/회원가입 로직은 미구현)
- 실무 환경 기준의 환경변수/설정/배포 구조 적용

---

## 구현된 기능 및 구조

- 카카오 OAuth2 인증 플로우 (Spring Security 기반)
    - `/oauth2/authorization/kakao`로 진입 시 카카오 인증 → 인증 성공 시 사용자 정보 콜백
    - 현재는 사용자 정보를 화면에만 출력, **DB 저장/회원가입 로직은 미구현**
- PostgreSQL 연동 및 JPA 설정 (테이블 자동 생성)
- Docker 기반 개발/운영 환경 (docker-compose, .env, nginx 프록시 등)
- 환경변수/민감정보 분리 및 실무 기준 관리

---

## 로컬 개발/운영 환경 세팅

### 1. 필수 설치
- Docker Desktop (WSL2 권장)
- Java 17 (Adoptium Temurin 권장)
- Git
- Gradle Wrapper (프로젝트 포함)
- (선택) psql, DBeaver 등 DB 클라이언트

### 2. 환경변수 파일 준비
- `.env.example`, `user-service/.env.example` 참고하여 실제 값을 `.env`, `user-service/.env`로 복사/입력
    - 카카오 OAuth2 Client ID/Secret, DB 비밀번호 등은 운영 담당자에게 별도 전달

### 3. 컨테이너 및 서버 실행
```bash
docker-compose up -d
```
> DB, user-service 컨테이너가 모두 기동되어야 정상 동작

### 4. 서버 직접 실행 (개발용)
```bash
./gradlew bootRun --args="--spring.profiles.active=local"
```

### 5. DB 접속 테스트
```bash
psql -h localhost -p 5432 -U mypoly -d mypoly
# 비밀번호: mypoly1234 (또는 .env에 지정된 값)
```

### 6. nginx 프록시/도메인 연동 (운영/테스트)
- `nginx-mypoly.conf` 참고
- 예시 도메인: `mypoly-test.o-r.kr` → 내부적으로 8082 포트로 프록시

---

## 프론트엔드(앱) 개발자 전달용 API/URL

- **카카오 로그인 시작 URL**
    - `{도메인}/oauth2/authorization/kakao`
    - 예시: http://mypoly-test.o-r.kr:8082/oauth2/authorization/kakao
    - 앱/웹에서 해당 URL로 이동(WebView 등) → 카카오 인증 → 콜백
- **로그인 성공 후 콜백 URL**
    - 기본값: `/login/success` (현재는 사용자 정보만 화면에 출력)
    - 실제 서비스에서는 이 콜백에서 JWT 발급, 회원가입/연동 등 추가 구현 필요

> 현재는 인증만 되고, 회원 데이터 저장/토큰 발급 등은 미구현 상태입니다.

---

## 주요 환경변수/설정 파일 설명

- `.env`, `.env.example` (루트): DB, 공통 인프라 환경변수
- `user-service/.env`, `.env.example`: 카카오 OAuth2, DB 비밀번호 등 서비스별 민감정보
- `docker-compose.yml`: 전체 서비스/DB 컨테이너 정의 및 환경변수 주입
- `user-service/src/main/resources/application.properties`: Spring Boot 서비스 설정 (환경변수 치환)
- `nginx-mypoly.conf`: 도메인 프록시 설정 예시

---

## 자주 발생하는 문제 및 해결법
- 5432 포트 충돌: 로컬 Postgres가 켜져 있으면 반드시 종료
- Docker Desktop 미실행 시 모든 docker 명령 실패
- DB 환경변수 변경 시 반드시 볼륨 삭제 후 재생성
- 도메인/프록시 연동 시 nginx 설정, hosts 파일, 방화벽 등 확인

---

## 협업/운영시 주의사항
- 민감정보(.env)는 반드시 git에 노출 금지, .env.example은 샘플로 git에 포함(운영 담당자 별도 전달)
- 카카오 OAuth2 Client ID/Secret, DB 비밀번호 등은 실제 운영값으로 교체 필요
- 회원가입/연동, JWT 발급 등은 추가 구현 필요(현재 인증만 동작)

---

## 문의/피드백
- 실무 환경/배포/연동/프론트엔드 API 등 추가 문의는 담당자에게 직접 연락
