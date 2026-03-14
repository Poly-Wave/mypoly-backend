# bill-service

`bill-service`는 MyPoly의 의안 카테고리와 사용자 관심 카테고리 저장 기능을 담당하는 서비스입니다.

현재 저장소 기준으로 bill-service는 아직 범위가 크지 않으며, 아래 기능에 집중되어 있습니다.

## 주요 책임

- 활성화된 의안 카테고리 목록 조회
- 로그인 사용자의 관심 카테고리 저장 / 갱신
- 온보딩 단계에서의 관심 카테고리 저장
- 관심 카테고리 저장 완료 후 `user-service`의 온보딩 상태 갱신
- JWT 인증 및 현재 세션 유효성 검증

## 사용 기술

- Spring Boot 3.1.6
- Spring Web
- Spring Data JPA
- Spring Validation
- Spring Security
- Spring Actuator
- Flyway
- QueryDSL
- springdoc-openapi
- Resilience4j
- Apache HttpClient 5 기반 RestTemplate

## 실행 프로필

- 기본 활성 profile: `local`
- 설정 파일
  - `src/main/resources/application.properties`
  - `src/main/resources/application-local.properties`
  - `src/main/resources/application-prod.properties`

### profile별 특징

#### local
- Swagger 활성화
- SQL 로그 / 포맷 로그 활성화
- 트랜잭션 로그 강화

#### prod
- Swagger 비활성화
- SQL 로그 최소화

## API 경로

컨트롤러 기준 base path는 `/categories`입니다.

로컬 기본값은 context path가 비어 있으므로 아래 경로를 그대로 사용합니다.

Kubernetes에서는 `SERVER_SERVLET_CONTEXT_PATH=/bills`가 주입되므로 외부 경로는 `/bills` prefix가 붙습니다.

예)
- 로컬: `/categories`
- K8s: `/bills/categories`

## 엔드포인트

### 1) 카테고리 목록 조회
- `GET /categories`
- 인증 불필요

동작
- `bill_categories.is_active=true` 인 카테고리만 조회
- `display_order` 오름차순 반환
- 응답에 icon URL 포함

icon URL은 아래 규칙으로 생성됩니다.

```text
{BILL_CATEGORY_ICON_BASE_URL}/bill-categories/{code}.webp
```

예)
```text
https://storage.googleapis.com/mypoly-assets-dev/bill-categories/DIGITAL.webp
```

### 2) 내 관심 카테고리 저장 / 갱신
- `POST /categories/interests`
- JWT 필요

동작 규칙
- 요청의 `categoryIds` 기준으로 전체 관심 카테고리를 갱신
- 기존 관심사 중 요청에 없는 항목은 삭제
- 새 항목은 추가
- 중복 ID는 제거
- 존재하지 않거나 비활성화된 카테고리 ID는 무시

### 3) 온보딩 전용 관심 카테고리 저장
- `POST /categories/onboarding/interests`
- JWT 필요

추가 동작
- 일반 관심사 저장과 같은 방식으로 저장
- 이후 `user-service`에 온보딩 상태 변경 요청
- 온보딩 상태가 허용되지 않는 경우 예외 처리

## 보안 정책

### 공개 API
보안 설정상 `GET /categories/**`는 로그인 없이 호출 가능합니다.

### 보호 API
아래는 JWT가 필요합니다.
- `POST /categories/interests`
- `POST /categories/onboarding/interests`

## JWT 검증 방식

bill-service는 JWT를 직접 발급하지 않습니다.

동작 방식은 아래와 같습니다.

1. `mypoly-security`의 `JwtAuthenticationFilter`가 access token을 파싱
2. JWT 서명을 `JWT_SECRET`으로 검증
3. JWT 안의 `userId`, `sid` 추출
4. `user-service` 내부 API(`/internal/auth/session/validate`)를 호출해 현재 세션 유효성 확인
5. 유효할 때만 인증 성공 처리

즉, bill-service에서 인증이 정상 동작하려면 아래 두 조건이 모두 필요합니다.
- JWT_SECRET이 user-service와 동일해야 함
- user-service가 네트워크상 접근 가능해야 함

## user-service 연동

bill-service는 내부적으로 `UserServiceClient`를 사용합니다.

### 사용 용도
- 현재 사용자 온보딩 상태 조회
- 현재 사용자 온보딩 상태 변경
- 현재 access token의 세션 유효성 확인

### 관련 설정값
- `user-service.url`
  - 기본값: `http://user-service:80/users/`
- `user-service.internal-url`
  - 기본값: `http://user-service:80/users`

### Resilience4j 설정
`RestTemplateConfig` 기준
- connect timeout: 5초
- response timeout: 10초
- retry: 최대 3회, 500ms 간격
- circuit breaker
  - failureRateThreshold: 50
  - waitDurationInOpenState: 10초
  - slidingWindowSize: 10
  - minimumNumberOfCalls: 5

## 필요한 환경변수

### 필수
- `SERVER_PORT`
- `SPRING_PROFILES_ACTIVE`
- `DB_URL`
- `DB_USERNAME`
- `DB_PASSWORD`
- `JWT_SECRET`

### 연동 / 선택
- `USER_SERVICE_URL`
- `USER_SERVICE_INTERNAL_URL`
- `SERVER_FORWARD_HEADERS_STRATEGY`
- `SERVER_SERVLET_CONTEXT_PATH` 또는 `BILL_SERVER_SERVLET_CONTEXT_PATH`
- `BILL_CATEGORY_ICON_BASE_URL`

## Swagger

Swagger는 **local profile에서만 활성화**됩니다.

### 로컬
- `http://localhost:8081/swagger-ui.html`
- `http://localhost:8081/v3/api-docs`

### Kubernetes
- `https://api-mypoly.kro.kr/bills/swagger-ui.html`
- `https://api-mypoly.kro.kr/bills/v3/api-docs`

Swagger에서 보호 API를 테스트하려면, 먼저 user-service에서 JWT를 발급받아 Authorize에 넣어야 합니다.

## 데이터베이스 스키마

사용 스키마: `bill_service`

### 주요 테이블
- `bill_categories`
- `user_bill_interests`

### 테이블 요약
#### bill_categories
- 카테고리 코드, 이름, 정렬 순서, 활성 여부 저장
- `icon_key`는 generated column
  - 규칙: `bill-categories/{code}.webp`

#### user_bill_interests
- 사용자별 관심 카테고리 저장
- `(user_id, category_id)` 유니크 제약 존재

## Flyway 마이그레이션

- `V1__init.sql`
- `V2__seed_bill_categories.sql`

기본 seed 카테고리는 다음 순서로 들어갑니다.
- DIGITAL
- SECURITY
- BROADCAST
- ECONOMY
- REAL_ESTATE
- TRANSPORT
- ENVIRONMENT
- MEDICAL
- WELFARE
- EDUCATION
- LABOR
- WOMEN
- FAMILY
- CHILD
- SEX_CRIME
- FOREIGN_DEFENSE
- LAW_ADMIN

## 로컬 실행 예시

### Docker Compose
루트에서 실행

```bash
docker compose up -d db user-service bill-service --build
```

### 단독 bootRun 예시
루트에서 실행

```bash
SPRING_PROFILES_ACTIVE=local \
SERVER_PORT=8081 \
DB_URL=jdbc:postgresql://localhost:5432/mypoly \
DB_USERNAME=mypoly \
DB_PASSWORD=your_password \
JWT_SECRET=your_access_secret \
USER_SERVICE_URL=http://localhost:8080/ \
USER_SERVICE_INTERNAL_URL=http://localhost:8080 \
./gradlew :bill-service:bootRun
```

> 로컬에서 user-service를 context path 없이 띄우는 기본 구조라면 `USER_SERVICE_URL=http://localhost:8080/`, `USER_SERVICE_INTERNAL_URL=http://localhost:8080`처럼 맞춰줘야 합니다. Docker Compose 환경에서는 기본값(`http://user-service:80/users/`, `http://user-service:80/users`)이 컨테이너 간 통신 기준으로 맞습니다.

## 참고 사항

- bill-service는 인증 관점에서 user-service에 의존성이 있습니다.
- `GET /categories`만 단독으로 보면 공개 API라서 user-service가 없어도 호출 가능하지만, 보호 API나 세션 검증 흐름은 user-service가 떠 있어야 정상 동작합니다.
