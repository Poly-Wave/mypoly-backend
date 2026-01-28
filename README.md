# mypoly-backend

MyPoly 서비스의 백엔드 모노레포(Gradle 멀티모듈)입니다.  
현재는 `user-service`(소셜 로그인/JWT)부터 구현을 진행하고 있으며, 향후 도메인/서비스를 추가로 분리해 확장하는 구조를 전제로 합니다.

## 현재 구현 범위
- `user-service`
  - Kakao OAuth2 로그인/회원가입
  - JWT 발급(성공 시) + 표준 JSON 응답(`ApiResponse`)
  - Controller API DTO ↔ Application(Command/Result) DTO 분리
  - Provider switch 제거(Provider 별 구현체 + Resolver 라우팅)
  - K8s Ingress rewrite 없이 **context-path(`/users`)** 로 운영

상세 동작/엔드포인트는 아래 문서를 참고하세요.
- `user-service/README.md`
- `README-k8s.md` (Kubernetes 배포/운영)

## 기술 스택
- Java 17
- Spring Boot **3.1.6** (Gradle plugin: `org.springframework.boot`)
- Gradle (멀티모듈)
- PostgreSQL
- Spring Security (OAuth2 Client)
- Docker / docker compose
- Kubernetes (Kustomize overlay)

## 디렉터리 구조
- `user-service/` : 소셜 로그인/JWT 서비스
- `k8s/` : Kustomize 기반 배포 리소스(base + overlays)
- `README-k8s.md` : K8s 배포 가이드(요약)
- `.env.example`, `user-service/.env.example` : 로컬 실행용 환경변수 템플릿

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
- 로그인 안내: `GET http://localhost:8080/login`
  - 응답의 `data`에 OAuth2 시작 URL이 내려옵니다.
- OAuth2 시작: `GET http://localhost:8080/oauth2/authorization/kakao`

> 로컬 기본 포트는 8080입니다. 필요하면 `user-service/.env`에서 `USER_SERVICE_PORT`로 조정합니다.

## 환경변수 관리 원칙
- 공통 인프라(예: PostgreSQL 계정/DB명)는 루트 `.env`에 둡니다.
- 서비스 전용 시크릿(예: Kakao Client Secret, JWT_SECRET)은 `user-service/.env`에 둡니다.
- 실제 운영 시에는 `.env`를 쓰기보다 K8s `Secret/ConfigMap` 또는 별도 시크릿 관리 방식을 권장합니다. (K8s 문서 참고)

## 개발 메모
- 모노레포 루트 README는 “프로젝트 홈/개요” 역할만 담당하고,
  서비스 내부 동작/엔드포인트는 서비스 디렉터리(`user-service/README.md`)에서 관리합니다.
- 빌드 산출물(`build/`, `bin/`)은 커밋 대상이 아닙니다.
