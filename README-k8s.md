# mypoly-backend Kubernetes 배포 가이드

이 문서는 현재 저장소에 포함된 `k8s/base` 기준으로 `mypoly-backend`를 Kubernetes에 배포하는 방법을 정리합니다.

본 문서는 **실제 저장소에 존재하는 매니페스트만 기준으로 작성**했습니다.

## 현재 K8s 디렉터리 구조

```text
k8s/
└── base/
    ├── kustomization.yaml
    ├── namespace.yaml
    ├── clusterissuers.yaml
    ├── api-mypoly-cert.yaml
    ├── infra/
    │   ├── jwt/
    │   └── postgres/
    └── services/
        ├── user-service/
        └── bill-service/
```

`k8s/base/kustomization.yaml`은 아래 리소스를 한 번에 포함합니다.

- namespace
- cert-manager 관련 리소스
  - `ClusterIssuer`
  - `Certificate`
- 공통 인프라
  - PostgreSQL
  - JWT Config / Secret
- 서비스
  - `user-service`
  - `bill-service`

## 배포 대상 리소스 개요

### 1) Namespace
- `namespace.yaml`
- 네임스페이스 이름: `mypoly`

### 2) 공통 인프라
#### PostgreSQL
- 위치: `k8s/base/infra/postgres`
- 구성
  - `postgres-service.yaml`
  - `postgres-pvc.yaml`
  - `postgres-deployment.yaml`
  - `secret.yaml`
- 서비스 이름: `mypoly-postgres`
- 포트: `5432`
- PVC 이름: `mypoly-postgres-pvc`
- `storageClassName`: `local-path`

#### JWT 공통 설정
- 위치: `k8s/base/infra/jwt`
- 구성
  - `configmap.yaml`
  - `secret.yaml`
- 주요 값
  - `JWT_EXPIRATION_MILLIS`
  - `REFRESH_JWT_EXPIRATION_MILLIS`
  - `JWT_SECRET`
  - `REFRESH_JWT_SECRET`

### 3) user-service
- 위치: `k8s/base/services/user-service`
- 구성
  - `configmap.yaml`
  - `secret.yaml`
  - `service.yaml`
  - `deployment.yaml`
  - `ingress.yaml`
- 외부 경로: `/users`
- 내부 Service 포트: `80 -> 8080`
- Deployment image: `ghcr.io/poly-wave/mypoly-user-service:0.1`
- 컨테이너 context path: `/users`

### 4) bill-service
- 위치: `k8s/base/services/bill-service`
- 구성
  - `configmap.yaml`
  - `secret.yaml`
  - `service.yaml`
  - `deployment.yaml`
  - `ingress.yaml`
- 외부 경로: `/bills`
- 내부 Service 포트: `80 -> 8080`
- Deployment image: `ghcr.io/poly-wave/mypoly-bill-service:0.1`
- 컨테이너 context path: `/bills`

## 인증서 / HTTPS 구성

현재 base에는 cert-manager + Let’s Encrypt 기준의 TLS 리소스가 포함되어 있습니다.

### ClusterIssuer
- 파일: `k8s/base/clusterissuers.yaml`
- 포함 리소스
  - `letsencrypt-staging`
  - `letsencrypt-prod`

두 Issuer 모두 `http01` solver + `ingress.class: nginx`를 사용합니다.

### Certificate
- 파일: `k8s/base/api-mypoly-cert.yaml`
- 리소스명: `api-mypoly-kro-kr`
- 대상 도메인: `api-mypoly.kro.kr`
- Secret 이름: `api-mypoly-kro-kr-tls`
- 현재 issuerRef: `letsencrypt-prod`

### Ingress
두 서비스 Ingress 모두 동일한 TLS Secret을 사용합니다.

- host: `api-mypoly.kro.kr`
- tls secret: `api-mypoly-kro-kr-tls`
- annotation
  - `nginx.ingress.kubernetes.io/ssl-redirect: "true"`
  - `nginx.ingress.kubernetes.io/force-ssl-redirect: "true"`

즉, 현재 구조는 **하나의 호스트 아래 path 기반 라우팅**입니다.

- `https://api-mypoly.kro.kr/users/**`
- `https://api-mypoly.kro.kr/bills/**`

## 배포 전 선행 조건

아래는 클러스터에 미리 준비되어 있어야 합니다.

### 필수
- Ingress NGINX Controller
- cert-manager (CRD 포함)
- `local-path` StorageClass 또는 이에 준하는 동일 이름 StorageClass

### 확인 예시

```bash
kubectl -n cert-manager get pods
kubectl get crd | grep cert-manager
kubectl get storageclass
```

## 적용 방법

저장소 기준으로는 `base`만 존재하므로 아래 명령으로 배포합니다.

```bash
kubectl apply -k k8s/base
```

## 배포 후 확인 포인트

### 1) 전체 리소스 확인

```bash
kubectl -n mypoly get all
kubectl -n mypoly get ingress
kubectl -n mypoly get pvc
kubectl -n mypoly get secret
```

### 2) 인증서 상태 확인

```bash
kubectl -n mypoly get certificate
kubectl -n mypoly describe certificate api-mypoly-kro-kr
kubectl -n mypoly get order,challenge
```

### 3) 서비스 헬스 체크

#### user-service
- readiness: `/users/actuator/health/readiness`
- liveness: `/users/actuator/health/liveness`

#### bill-service
- readiness: `/bills/actuator/health/readiness`
- liveness: `/bills/actuator/health/liveness`

## 서비스별 ConfigMap / Secret 요약

### user-service ConfigMap
- `SPRING_PROFILES_ACTIVE`
- `SERVER_PORT`
- `SERVER_FORWARD_HEADERS_STRATEGY`
- `GOOGLE_ALLOWED_AUDIENCES`
- `APPLE_ALLOWED_AUDIENCES`
- `DB_URL`

### user-service Secret
- `KAKAO_CLIENT_ID`
- `KAKAO_CLIENT_SECRET`

### bill-service ConfigMap
- `SPRING_PROFILES_ACTIVE`
- `SERVER_PORT`
- `SERVER_FORWARD_HEADERS_STRATEGY`
- `DB_URL`
- `USER_SERVICE_INTERNAL_URL`
- `BILL_CATEGORY_ICON_BASE_URL`

### bill-service Secret
- 현재 `stringData: {}`로 비어 있음

### 공통 DB Secret
- `DB_NAME`
- `DB_USERNAME`
- `DB_PASSWORD`

### 공통 JWT Secret / ConfigMap
- `JWT_SECRET`
- `REFRESH_JWT_SECRET`
- `JWT_EXPIRATION_MILLIS`
- `REFRESH_JWT_EXPIRATION_MILLIS`

## 현재 서비스 간 연동 구조

### user-service
- DB 직접 접근
- JWT / Refresh Token 발급
- 세션 상태(auth_session_id) 보유

### bill-service
- DB 직접 접근
- JWT는 공통 `JWT_SECRET`으로 검증
- 세션 유효성은 `user-service` 내부 API 호출로 검증
- 온보딩 상태 변경 시 `user-service` API 호출

즉, bill-service의 인증 관련 정상 동작에는 user-service가 함께 떠 있어야 합니다.

## 외부 접근 경로

### user-service
- Swagger UI: `https://api-mypoly.kro.kr/users/swagger-ui.html`
- OpenAPI: `https://api-mypoly.kro.kr/users/v3/api-docs`

### bill-service
- Swagger UI: `https://api-mypoly.kro.kr/bills/swagger-ui.html`
- OpenAPI: `https://api-mypoly.kro.kr/bills/v3/api-docs`

> 다만 운영 profile로 올리면 Swagger는 비활성화되는 구성이 기본입니다. 현재 K8s ConfigMap은 `SPRING_PROFILES_ACTIVE=local`로 되어 있으므로, base 그대로 배포하면 Swagger가 켜집니다.

## 주의 사항

### 1) 현재 base는 운영 비밀값이 포함될 수 있음
저장소에는 `secret.yaml.example`도 있지만, 실제 `secret.yaml` 파일들도 함께 존재합니다. 운영 저장소에서는 예시 파일만 관리하고 실제 Secret은 별도 방식으로 주입하는 편이 안전합니다.

### 2) 현재 image tag는 0.1로 고정
- `user-service`: `ghcr.io/poly-wave/mypoly-user-service:0.1`
- `bill-service`: `ghcr.io/poly-wave/mypoly-bill-service:0.1`

새 이미지 배포 시 Deployment image를 함께 갱신해야 합니다.

### 3) PostgreSQL은 단일 Deployment 구성
- replicas: 1
- PVC 기반 저장소 사용
- 운영 고가용성 구조는 아직 포함되어 있지 않습니다.

### 4) overlay 구조는 현재 없음
예전 문서와 달리, 현재 저장소에는 `k8s/overlays/dev`, `k8s/overlays/prod`가 존재하지 않습니다. 배포 문서는 반드시 `k8s/base` 기준으로 이해해야 합니다.
