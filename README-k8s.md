# mypoly-backend Kubernetes 배포 가이드

Kustomize 기반으로 `base` + `overlays(dev/prod)` 형태로 배포 리소스를 관리합니다.

## 디렉터리 구조
- `k8s/base` : 공통 리소스(Deployment, Service, ConfigMap, Secret, Ingress, Namespace 등)
- `k8s/overlays/dev` : 개발 환경 패치
- `k8s/overlays/prod` : 운영 환경 패치

## 핵심 설계 (Ingress rewrite 제거)
- Ingress는 `/users` Prefix로 라우팅만 담당합니다.
- 애플리케이션은 `SERVER_SERVLET_CONTEXT_PATH=/users`로 동작하여,  
  별도의 rewrite 없이 `/users/**` 아래에서 정상 동작합니다.

## 배포 방법

### 1) Namespace 생성
```sh
kubectl apply -f k8s/base/namespace.yaml
```

### 2) (선택) 이미지 준비
현재 base는 예시로 `mypoly-backend-user-service:latest`를 사용합니다.
원격 클러스터에서는 레지스트리(GAR 등)에 이미지를 push하고, overlay에서 image를 교체하는 방식을 권장합니다.

### 3) ConfigMap/Secret 설정
- `k8s/base/configmap.yaml` : profile, port, forward headers 등
- `k8s/base/secret.yaml` : DB, Kakao, JWT 시크릿

주의:
- Secret에는 민감정보가 포함됩니다.
- 공개 저장소에 올리는 경우 `secret.yaml`을 템플릿(example)로만 두고, 실제 값은 별도 전달/관리하세요.

### 4) Kustomize로 배포
```sh
# dev 환경
kubectl apply -k k8s/overlays/dev

# prod 환경
kubectl apply -k k8s/overlays/prod
```

## Ingress
- `k8s/base/ingress.yaml`
  - Host: `api-mypoly.kro.kr`
  - Path: `/users` (Prefix)

예시:
- 로그인 안내: `GET http://api-mypoly.kro.kr/users/login`
- OAuth2 시작: `GET http://api-mypoly.kro.kr/users/oauth2/authorization/kakao`

## Health 체크
Actuator health만 노출하고, k8s probe는 아래 엔드포인트를 사용합니다.
- liveness: `/users/actuator/health/liveness`
- readiness: `/users/actuator/health/readiness`
