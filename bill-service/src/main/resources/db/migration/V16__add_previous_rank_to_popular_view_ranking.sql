SET search_path TO bill_service;

ALTER TABLE bill_popular_view_ranking
    ADD COLUMN previous_rank SMALLINT;

COMMENT ON COLUMN bill_popular_view_ranking.previous_rank IS '직전 배치 실행 시점 순위 (동일 주차 기준)';
