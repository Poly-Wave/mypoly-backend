SET search_path TO bill_service;

-- AI 요약을 한 덩어리(summary) 대신 3줄로 분리 저장한다.
-- 기존 summary 컬럼은 백필 완료 전까지 하위호환/폴백용으로 유지한다(추후 제거 예정).
ALTER TABLE bill_ai_analyses
    ADD COLUMN IF NOT EXISTS summary1 TEXT,
    ADD COLUMN IF NOT EXISTS summary2 TEXT,
    ADD COLUMN IF NOT EXISTS summary3 TEXT;

COMMENT ON COLUMN bill_ai_analyses.summary1 IS 'AI 3줄 요약 1번째 줄';
COMMENT ON COLUMN bill_ai_analyses.summary2 IS 'AI 3줄 요약 2번째 줄';
COMMENT ON COLUMN bill_ai_analyses.summary3 IS 'AI 3줄 요약 3번째 줄';
