SET search_path TO bill_service;

-- 3줄 요약(summary1/2/3)으로 완전히 대체되어 더 이상 필요 없는 구 컬럼을 제거한다.
ALTER TABLE bill_ai_analyses
    DROP COLUMN IF EXISTS summary;
