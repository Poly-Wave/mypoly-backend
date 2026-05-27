# 알림 기능 운영 RUNBOOK

mypoly k8s 클러스터(namespace=`mypoly`) 에 알림 기능을 처음 올릴 때 / 운영 중 토글할 때 / 사고 났을 때 절차.

> 기존 컨벤션: `k8s/base/` kustomize 기반 + 서비스별 디렉토리.
> 새로 추가된 것: `infra/internal-api/`, `services/notification-service/`.

---

## 0. 사전 준비

- [ ] 새 이미지 빌드 + push:
  ```bash
  echo $GHCR_TOKEN | docker login ghcr.io -u <user> --password-stdin
  docker build -t ghcr.io/poly-wave/mypoly-user-service:0.2          -f user-service/Dockerfile .
  docker build -t ghcr.io/poly-wave/mypoly-bill-service:0.4          -f bill-service/Dockerfile .
  docker build -t ghcr.io/poly-wave/mypoly-notification-service:0.1  -f notification-service/Dockerfile .
  docker push ghcr.io/poly-wave/mypoly-user-service:0.2
  docker push ghcr.io/poly-wave/mypoly-bill-service:0.4
  docker push ghcr.io/poly-wave/mypoly-notification-service:0.1
  ```
  배포할 deployment.yaml 의 image 태그를 위 새 태그로 맞출 것.

- [ ] 비밀값 파일 준비 (절대 git 커밋 X):
  ```bash
  cp k8s/base/infra/internal-api/secret.yaml.example          k8s/base/infra/internal-api/secret.yaml
  cp k8s/base/services/notification-service/secret.yaml.example k8s/base/services/notification-service/secret.yaml
  # 두 파일의 placeholder 를 실제 값으로 채울 것.
  # 권장: openssl rand -hex 32 로 internal-api 의 3개 키 생성.
  ```

- [ ] DB 권한 확인 (mypoly 계정이 새 schema 만들 수 있어야 함):
  ```bash
  kubectl exec -n mypoly deploy/mypoly-postgres -- psql -U mypoly -d mypoly -c \
    "SELECT has_database_privilege('mypoly','mypoly','CREATE');"
  # 't' 가 나와야 정상. 'f' 면:
  kubectl exec -n mypoly deploy/mypoly-postgres -- psql -U postgres -c \
    "GRANT CREATE ON DATABASE mypoly TO mypoly;"
  ```

---

## 1. 첫 배포

### 1-1. 렌더링 미리보기

```bash
kubectl kustomize k8s/base | head -200
# 새 리소스 (mypoly-internal-api-secret, notification-service deployment 등) 가 들어있는지 확인
```

### 1-2. 적용

```bash
kubectl apply -k k8s/base
```

단계별로 가고 싶다면:
```bash
# (1) 새 infra
kubectl apply -k k8s/base/infra/internal-api

# (2) 기존 서비스 갱신 (envFrom 에 internal-api secret 추가 + 새 이미지 태그)
kubectl apply -k k8s/base/services/user-service
kubectl apply -k k8s/base/services/bill-service
kubectl rollout status -n mypoly deploy/user-service
kubectl rollout status -n mypoly deploy/bill-service

# (3) 신규 notification-service
kubectl apply -k k8s/base/services/notification-service
kubectl rollout status -n mypoly deploy/notification-service
```

### 1-3. 자동 적용 확인

부팅 시 자동으로:
- **user-service**: V7 마이그레이션 (`users` 에 마일스톤 컬럼 3개 추가)
- **notification-service**: `notification_service` schema 생성 + V1 마이그레이션 + **정책 7개 자동 시드 (모두 READY)**

```bash
kubectl logs -n mypoly deploy/notification-service | grep -E "Migrating|Seeded"
# "Successfully applied 1 migration" + "Seeded system notification policy" × 7
```

DB 직접 확인:
```bash
kubectl exec -n mypoly deploy/mypoly-postgres -- psql -U mypoly -d mypoly -c \
  "SELECT id, policy_key, status FROM notification_service.notification_policies ORDER BY id;"
# 7행 모두 READY 여야 정상
```

---

## 2. 정책 ACTIVE 전환 (운영자 명시 작업)

스케줄러 ON 전에 정책을 ACTIVE 로 바꿔야 발송 시작.

```bash
# notification-service 임시 접근
kubectl port-forward -n mypoly svc/notification-service 18082:80 &
PF=$!

# 어드민 키 (secret 에서 추출)
ADMIN_KEY=$(kubectl get secret -n mypoly mypoly-internal-api-secret \
              -o jsonpath='{.data.NOTIFICATION_ADMIN_API_KEY}' | base64 -d)

# 7개 정책 모두 ACTIVE
curl -s "http://localhost:18082/internal/notification-policies?size=20" \
  -H "X-Admin-Api-Key: $ADMIN_KEY" \
  | jq -r '.policies[].policyId' \
  | xargs -I{} curl -s -X PATCH "http://localhost:18082/internal/notification-policies/{}/status" \
      -H "Content-Type: application/json" \
      -H "X-Admin-Api-Key: $ADMIN_KEY" \
      -d '{"status":"ACTIVE"}' > /dev/null

# (선택) 본문/타이틀 수정이 필요하면 PATCH /internal/notification-policies/{id} 으로 변경 후 ACTIVE
kill $PF
```

---

## 3. 스케줄러 ON

```bash
kubectl patch configmap -n mypoly notification-service-config \
  --type merge -p '{"data":{"NOTIFICATION_SCHEDULER_ENABLED":"true"}}'

kubectl rollout restart -n mypoly deploy/notification-service
kubectl rollout status  -n mypoly deploy/notification-service
```

이 시점부터:

| 행 | cron | 무엇 |
|---|---|---|
| 1·2 | 매일 12:00 KST | 온보딩 D+1 리마인더 |
| 3 | 매일 09:00 KST | 관심 카테고리 매칭 신규 안건 |
| 4·5 | 매일 18:00 KST | 홈 broadcast (요즘 핫한 / 최근 30일 인기) |
| 6 | 10분 polling | 북마크 의안 단계 변경 |
| 7 | 매일 12:00 KST | 북마크 D+1 미투표 |

---

## 4. 운영 중 토글

### 긴급 정지 (모든 알림 OFF)

```bash
kubectl patch configmap -n mypoly notification-service-config \
  --type merge -p '{"data":{"NOTIFICATION_SCHEDULER_ENABLED":"false"}}'
kubectl rollout restart -n mypoly deploy/notification-service
```

### 특정 정책만 비활성

```bash
curl -X PATCH "http://localhost:18082/internal/notification-policies/{id}/status" \
  -H "Content-Type: application/json" -H "X-Admin-Api-Key: $ADMIN_KEY" \
  -d '{"status":"INACTIVE"}'
```

다음 스케줄러 실행 때 자동 skip. 이미 발급된 알림은 영향 없음.

### 정책 본문 수정

```bash
curl -X PATCH "http://localhost:18082/internal/notification-policies/{id}" \
  -H "Content-Type: application/json" -H "X-Admin-Api-Key: $ADMIN_KEY" \
  -d '{
        "name":"...","channel":"PUSH","category":"BILL",
        "body":"새 본문 ...",
        "landingType":"BILL_DETAIL"
      }'
```

발급 시점 스냅샷이라 이미 발급된 알림은 영향 없음. 이후 발급분부터 새 본문 적용.

---

## 5. 트러블슈팅

### "알림이 안 와요"

체크 순서:
1. `kubectl get deploy -n mypoly notification-service` → replica 1/1?
2. `kubectl logs -n mypoly deploy/notification-service --tail=200` → 에러?
3. ConfigMap 확인:
   ```bash
   kubectl get cm -n mypoly notification-service-config -o yaml | grep ENABLED
   ```
4. 정책 ACTIVE 인가? `curl ... /internal/notification-policies | jq '.policies[] | {policyKey, status}'`
5. segment API 직접 호출해서 발급 후보가 있는지:
   ```bash
   kubectl port-forward -n mypoly svc/user-service 18080:80 &
   USER_KEY=$(kubectl get secret -n mypoly mypoly-internal-api-secret -o jsonpath='{.data.USER_SERVICE_INTERNAL_API_KEY}' | base64 -d)
   curl -H "X-Internal-Api-Key: $USER_KEY" \
     "http://localhost:18080/users/internal/segments/onboarding-completed"
   ```
6. `user_notifications.dedup_key` 충돌 (이미 발급) → 정상.

### "/internal/** 가 외부에서 호출됨"

- `kubectl get ingress -n mypoly notification-service-ingress -o yaml | grep -A 3 server-snippet`
- nginx-ingress 의 `allow-snippet-annotations` 가 true 여야 server-snippet 동작:
  ```bash
  kubectl get cm -n ingress-nginx ingress-nginx-controller -o yaml | grep snippet
  ```

### Flyway 마이그레이션 실패

- mypoly 계정이 새 schema 를 만들 권한 있는지:
  ```bash
  kubectl exec -n mypoly deploy/mypoly-postgres -- psql -U postgres -c \
    "GRANT CREATE ON DATABASE mypoly TO mypoly;"
  ```

---

## 6. 환경별 차이

| 항목 | dev | prod |
|---|---|---|
| `SPRING_PROFILES_ACTIVE` | `local`/`dev` | `prod` |
| `NOTIFICATION_SCHEDULER_ENABLED` | 원할 때만 `true` | 최초 `false`, 정책 ACTIVE 후 `true` |
| `NOTIFICATION_DEV_TRIGGER_ENABLED` | `true` (테스트 편의) | **반드시 `false`** |
| `SOCIAL_DEV_AUTH_ENABLED` | `true` | **반드시 `false`** |
| `USER_DEV_DELETE_ENABLED` | `true` | **반드시 `false`** |
| Admin API 키 | 단순 키 OK | **`openssl rand -hex 32`** |
| JWT secret | 약해도 동작 | **32+ 바이트 강한 랜덤** |
| ingress `/dev-**` / `/internal/**` 차단 | 선택 | **반드시 차단** |

---

## 7. 시스템 정책 7개 식별

| policy_key | 노션 행 | cron | dedupKey |
|---|---|---|---|
| ONBOARDING_NICKNAME_REMIND_D1 | 1 | 매일 12:00 KST | `{key}:{userId}` (평생 1회) |
| ONBOARDING_CATEGORY_REMIND_D1 | 2 | 매일 12:00 KST | `{key}:{userId}` (평생 1회) |
| HOME_NEW_INTEREST_AGENDA_DAILY | 3 | 매일 09:00 KST | `{key}:{userId}:{date(KST)}` (하루 1회) |
| HOME_TRENDING_AGENDA_DAILY | 4 | 매일 18:00 KST | `{key}:{userId}:{date(KST)}` (하루 1회) |
| HOME_RECENT_30D_POPULAR_DAILY | 5 | 매일 18:00 KST | `{key}:{userId}:{date(KST)}` (하루 1회) |
| BILL_STAGE_CHANGE_NOTIFY | 6 | 10분 polling | `{key}:{userId}:{billId}:{toStageCode}` (단계 전이당 1회) |
| BILL_BOOKMARK_NO_VOTE_REMIND_D1 | 7 | 매일 12:00 KST | `{key}:{userId}:{billId}` (의안당 1회) |
