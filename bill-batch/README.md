# bill-batch

mypoly-backend 내부의 별도 Python 배치 서비스.

역할:
- 국회 의안 수집
- AI headline / summary / category / axis 생성
- bill_service 스키마의 bill_* 테이블 적재
- Kubernetes CronJob 실행

## 로컬 실행

```bash
cd bill-batch
python -m venv .venv
source .venv/bin/activate
pip install -r requirements.txt

cp .env.example .env
python -m app.main