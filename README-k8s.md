# mypoly-backend Kubernetes 배포/운영 가이드

이 문서는 `mypoly-backend`를 Kubernetes에 배포할 때 필요한 **Ingress 라우팅**, **HTTPS(TLS) 적용**, **Let’s Encrypt 인증서 자동 갱신**까지를 한 번에 정리한 가이드입니다.

## 최근 변경점 (이전 README-k8s 대비)
- **HTTPS/TLS 적용 방식 추가**
  - `cert-manager` + Let’s Encrypt(LE) + `ClusterIssuer`/`Certificate` 리소스로 자동 발급
  - Ingress에서 TLS Secret을 참조하도록 구성
  - HTTP → HTTPS 강제 리다이렉트 적용
- **인증서 리소스가 base에 포함**
  - `k8s/base`에 `clusterissuers.yaml`, `api-mypoly-cert.yaml`이 추가됨

---

## 디렉터리 구조
- `k8s/base` : 공통 리소스(Deployment, Service, ConfigMap, Secret, Ingress, Namespace, Issuer/Certificate 등)
- `k8s/overlays/dev` : 개발 환경 패치(프로필, 이미지 등)
- `k8s/overlays/prod` : 운영 환경 패치(레플리카, 이미지 등)

## 핵심 설계
### 1) Ingress rewrite 제거
- Ingress는 `/users` Prefix로 라우팅만 담당합니다.
- 애플리케이션은 `SERVER_SERVLET_CONTEXT_PATH=/users`로 동작하여,
  별도의 rewrite 없이 `/users/**` 아래에서 정상 동작합니다.

### 2) Ingress 뒤(Reverse Proxy)에서 OAuth2 redirect URL 안정화
- OAuth2 redirect-uri가 `{baseUrl}/login/oauth2/code/{registrationId}` 형태이므로,
  Ingress 뒤에서 `baseUrl`(scheme/host)을 정확히 잡는게 중요합니다.
- 이를 위해 `ConfigMap`에 아래 설정을 넣습니다.
  - `SERVER_FORWARD_HEADERS_STRATEGY=framework`

---

## 사전 준비(필수)
아래 2가지는 **클러스터에 반드시 먼저 설치**되어 있어야 합니다.

1) **Ingress-NGINX** (ingressClassName: `nginx`)
2) **cert-manager** (CRD 포함)

### cert-manager가 설치되어 있는지 확인
```bash
kubectl -n cert-manager get pods
kubectl get crd | grep cert-manager
```

> cert-manager가 없는데 `k8s/base`를 apply하면 `ClusterIssuer/Certificate`에서 실패합니다.

---

## 배포 방법

### 0) (중요) Secret 관리
- `k8s/base/secret.yaml`에는 DB/OAuth/JWT 등 민감정보가 들어갑니다.
- 공개 저장소라면 **`secret.yaml` 대신 `secret.yaml.example`만 커밋**하고,
  실제 `secret.yaml`은 별도 전달/시크릿 관리(ExternalSecrets/SealedSecrets 등)를 권장합니다.

### 1) 빠른 검증(베이스만 적용)
`k8s/base`는 네임스페이스부터 인그레스까지 전부 포함되어 있어, 검증용으로는 한 방에 올릴 수 있습니다.

```bash
kubectl apply -k k8s/base
```

### 2) 환경별 배포(overlay 권장)
```bash
# dev
kubectl apply -k k8s/overlays/dev

# prod
kubectl apply -k k8s/overlays/prod
```

> overlay에서는 주로 `SPRING_PROFILES_ACTIVE`, `replicas`, `image` 등을 교체합니다.

---

## HTTPS(TLS) 적용 구조 (cert-manager + Let’s Encrypt)

### 구성 요소 요약
- `k8s/base/clusterissuers.yaml`
  - `letsencrypt-staging`: **테스트용(브라우저 신뢰 X)** / 레이트리밋이 훨씬 느슨
  - `letsencrypt-prod`: **실서비스용(브라우저 신뢰 O)** / 레이트리밋 존재
- `k8s/base/api-mypoly-cert.yaml`
  - 특정 도메인(`api-mypoly.kro.kr`)에 대한 인증서를 발급받아 `secretName`에 저장해달라는 요청
- `k8s/base/ingress.yaml`
  - `spec.tls[].secretName`으로 TLS Secret을 참조
  - HTTP → HTTPS 리다이렉트 강제

### 왜 staging → prod 순서로 하냐?
Let’s Encrypt **운영(Prod) 환경은 레이트리밋(발급 제한)**이 있습니다.
- 설정/Ingress/DNS가 완전히 안정화되기 전엔 발급을 여러 번 시도하게 되기 쉬워서,
  바로 prod로 가면 제한에 걸릴 확률이 높습니다.
- staging으로 먼저 “구성이 정상인지” 검증한 뒤, prod로 한 번만 발급받는 방식이 안전합니다.

> 참고: `kro.kr`처럼 "registered domain"을 여러 사람이 공유하는 도메인(서브도메인 포함)에서는
> 다른 사람의 발급량까지 합산되어 레이트리밋에 더 쉽게 걸릴 수 있습니다.

---

## Let’s Encrypt 인증서 갱신은 자동인가?
**자동 갱신입니다.** 3개월마다 사람이 `kubectl apply`로 재발급할 필요가 없습니다.

### 동작 원리(요약)
1) `Certificate` 리소스가 존재하는 한 cert-manager가 만료/갱신 시점을 추적합니다.
2) 만료 전에 자동으로 재발급을 시도합니다.
   - `kubectl describe certificate ...`에 `Renewal Time`이 표시됩니다.
3) 재발급이 성공하면 **같은 `secretName`(TLS secret)** 안의 인증서/키가 갱신됩니다.
4) Ingress-NGINX가 Secret 변경을 감지하여 HTTPS에 즉시 반영합니다.

### 갱신/만료 확인
```bash
kubectl -n mypoly describe certificate api-mypoly-kro-kr
kubectl -n mypoly get certificate
```

---

## 리소스별 역할(한 줄 설명)
- `namespace.yaml`: `mypoly` 네임스페이스
- `configmap.yaml`: 프로필/포트/forward-headers 등 "비시크릿" 설정
- `secret.yaml`: DB/OAuth/JWT 등 시크릿
- `postgres.yaml`: 검증용 Postgres(현재 `emptyDir` → 운영은 PVC 권장)
- `service.yaml`: `user-service` ClusterIP 서비스
- `deployment.yaml`: `user-service` 배포(프로브 포함)
- `ingress.yaml`: 외부 접근(Host+Path 라우팅) + TLS + HTTP→HTTPS 리다이렉트
- `clusterissuers.yaml`: Let’s Encrypt 발급자(스테이징/프로덕션)
- `api-mypoly-cert.yaml`: 특정 도메인 인증서 발급 요청(Secret 생성/갱신)

---

## 접속/엔드포인트
- Host: `api-mypoly.kro.kr`
- Path: `/users` (Prefix)

예시:
- Swagger UI: `https://api-mypoly.kro.kr/users/swagger-ui.html`
- OpenAPI JSON: `https://api-mypoly.kro.kr/users/v3/api-docs`
- OAuth2 시작(공식): `https://api-mypoly.kro.kr/users/auth/kakao`

---

## 헬스 체크
Actuator health만 노출하고, k8s probe는 아래 엔드포인트를 사용합니다.
- liveness: `/users/actuator/health/liveness`
- readiness: `/users/actuator/health/readiness`

---

## 트러블슈팅(자주 터지는 것들)

### 1) 인증서가 Ready=False
```bash
kubectl -n mypoly get certificate,order,challenge
kubectl -n mypoly describe certificate api-mypoly-kro-kr
```

- `rateLimited`(429) : Let’s Encrypt 발급 제한
  - staging으로 먼저 검증하고, prod 발급은 제한 해제 이후에 시도
- `challenge`가 pending : 80 포트 접근 불가 / DNS 불일치 / ingress class 불일치가 흔함

### 2) Secret 충돌 경고(IncorrectCertificate)
동일한 `secretName`을 가리키는 `Certificate`가 2개 생기면 충돌합니다.
- 해결: 중복 Certificate를 삭제하거나 secretName을 분리

---

## 운영 체크리스트(짧게)
- Postgres는 `emptyDir`라서 Pod 재시작 시 데이터가 사라질 수 있습니다 → 운영은 PVC로 교체
- Swagger는 운영에서 꺼도 됩니다(`OPENAPI_ENABLED=false`, `SWAGGER_UI_ENABLED=false`)
- Secret은 Git에 직접 올리지 않는 방식 권장
