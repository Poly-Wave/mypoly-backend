SET search_path TO bill_service;

-- 공동발의자 스크래핑을 "시도해서 확인 완료"한 시각. 결과가 0명이었던 경우와
-- 아직 시도하지 못한/실패한 경우를 구분하기 위한 컬럼(둘 다 bill_proposers에
-- 공동발의자 행이 없다는 점에서는 동일하므로, 존재 여부만으로는 구분 불가).
ALTER TABLE bills ADD COLUMN proposer_scrape_checked_at TIMESTAMPTZ;
