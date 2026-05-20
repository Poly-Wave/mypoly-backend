#!/usr/bin/env bash
# ============================================================================
# 시스템 알림 정책 일괄 ACTIVE 전환 스크립트
#
# 용도:
#   notification-service 가 시드한 정책(SystemNotificationPolicySeeder)은
#   기본적으로 status=READY 로 들어간다. 운영자가 본문/타이틀을 한번 검토한 뒤
#   이 스크립트로 일괄 ACTIVE 전환해야 스케줄러가 실제 발송을 시작한다.
#
# 사용처:
#   - 환경(prod/dev) 최초 1회
#   - DB 를 새로 깔아 재시드된 직후 1회
#
# 멱등성:
#   이미 ACTIVE 인 정책에 다시 PATCH 해도 결과는 동일(ACTIVE 유지).
#
# 사전 준비:
#   - kubectl 컨텍스트가 대상 클러스터로 설정돼 있어야 함
#   - jq, curl 설치 필요
#   - notification-service deployment / svc 가 mypoly namespace 에 떠 있어야 함
#
# 실행:
#   bash k8s/scripts/activate-notification-policies.sh
# ============================================================================
set -euo pipefail

NAMESPACE="mypoly"
SVC="notification-service"
SECRET="mypoly-internal-api-secret"
SECRET_KEY="NOTIFICATION_ADMIN_API_KEY"
LOCAL_PORT="18082"

require_cmd() {
  if ! command -v "$1" >/dev/null 2>&1; then
    echo "[ERROR] 필수 명령어 '$1' 가 PATH 에 없습니다. 설치 후 다시 실행해주세요." >&2
    exit 1
  fi
}

require_cmd kubectl
require_cmd curl
require_cmd jq

# notification-service svc 가 있는지 빠르게 확인 (없으면 의미 있는 메시지)
if ! kubectl -n "$NAMESPACE" get svc "$SVC" >/dev/null 2>&1; then
  echo "[ERROR] svc/$SVC 를 namespace=$NAMESPACE 에서 찾을 수 없습니다." >&2
  echo "        kubectl get svc -n $NAMESPACE 로 상태 확인 바랍니다." >&2
  exit 1
fi

echo "[1/4] port-forward 기동 (localhost:${LOCAL_PORT} -> svc/${SVC}:80)"
kubectl port-forward -n "$NAMESPACE" "svc/${SVC}" "${LOCAL_PORT}:80" >/dev/null 2>&1 &
PF_PID=$!

# 스크립트 종료 시 port-forward 정리 보장
cleanup() {
  if kill -0 "$PF_PID" >/dev/null 2>&1; then
    kill "$PF_PID" >/dev/null 2>&1 || true
    wait "$PF_PID" 2>/dev/null || true
  fi
}
trap cleanup EXIT INT TERM

# port-forward 준비 대기 (최대 ~10초)
for i in $(seq 1 20); do
  if curl -sf -o /dev/null "http://localhost:${LOCAL_PORT}/actuator/health/readiness"; then
    break
  fi
  sleep 0.5
  if [ "$i" -eq 20 ]; then
    echo "[ERROR] port-forward 가 ${LOCAL_PORT} 에 살아나지 않습니다." >&2
    exit 1
  fi
done

echo "[2/4] admin key 추출"
ADMIN_KEY="$(kubectl get secret -n "$NAMESPACE" "$SECRET" \
              -o jsonpath="{.data.${SECRET_KEY}}" | base64 -d)"
if [ -z "$ADMIN_KEY" ]; then
  echo "[ERROR] secret/${SECRET} 에 ${SECRET_KEY} 값이 비어 있습니다." >&2
  exit 1
fi

echo "[3/4] 정책 ACTIVE 전환"
POLICY_IDS_JSON="$(curl -sf "http://localhost:${LOCAL_PORT}/internal/notification-policies?size=100" \
                     -H "X-Admin-Api-Key: $ADMIN_KEY")"

POLICY_IDS=( $(echo "$POLICY_IDS_JSON" | jq -r '.policies[].policyId') )

if [ "${#POLICY_IDS[@]}" -eq 0 ]; then
  echo "[ERROR] 조회된 정책이 0건입니다. SystemNotificationPolicySeeder 가 실행됐는지 확인하세요." >&2
  exit 1
fi

echo "       대상 정책 수: ${#POLICY_IDS[@]}"
for ID in "${POLICY_IDS[@]}"; do
  HTTP_CODE="$(curl -s -o /dev/null -w '%{http_code}' \
                 -X PATCH "http://localhost:${LOCAL_PORT}/internal/notification-policies/${ID}/status" \
                 -H "Content-Type: application/json" \
                 -H "X-Admin-Api-Key: $ADMIN_KEY" \
                 -d '{"status":"ACTIVE"}')"
  if [ "$HTTP_CODE" != "200" ] && [ "$HTTP_CODE" != "204" ]; then
    echo "[ERROR] 정책 id=${ID} ACTIVE 전환 실패 (HTTP ${HTTP_CODE})" >&2
    exit 1
  fi
  echo "       - id=${ID} → ACTIVE"
done

echo "[4/4] 현재 상태 확인"
curl -sf "http://localhost:${LOCAL_PORT}/internal/notification-policies?size=100" \
  -H "X-Admin-Api-Key: $ADMIN_KEY" \
  | jq -r '.policies[] | "  - id=\(.policyId) key=\(.policyKey) status=\(.status)"'

echo
echo "완료. 다음 단계는 RUNBOOK-notifications.md '3. 스케줄러 ON' 항목 참조."
