# mypoly-backend Kubernetes 배포 가이드

## 디렉터리 구조

- k8s/base: 공통 리소스(Deployment, Service, ConfigMap, Secret, Ingress 등)
- k8s/overlays/dev: 개발 환경 오버레이
- k8s/overlays/prod: 운영 환경 오버레이

## 배포 방법

### 1. 이미지 빌드 및 레지스트리 푸시
- Dockerfile 기반으로 이미지 빌드 후 레지스트리에 푸시

### 2. ConfigMap/Secret 값 설정
- base64 인코딩된 시크릿(JWT 등) 및 환경별 변수 지정

### 3. Kustomize로 배포
```sh
# dev 환경
kubectl apply -k k8s/overlays/dev

# prod 환경
kubectl apply -k k8s/overlays/prod
```

### 4. Ingress 도메인/인증서 설정
- k8s/base/ingress.yaml의 <YOUR_DOMAIN>을 실제 도메인으로 변경
- 인증서(HTTPS) 필요시 Ingress Controller 및 TLS Secret 추가

## 참고
- 네임스페이스: mypoly
- 서비스명: user-service
- 포트: 8080 (내부), 80 (Service)
- 환경 변수/시크릿은 ConfigMap, Secret에서 관리
