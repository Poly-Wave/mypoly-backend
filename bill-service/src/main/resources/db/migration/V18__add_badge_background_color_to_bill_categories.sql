SET search_path TO bill_service;

-- 안건 카테고리 뱃지 디자인용 배경색.
-- 기존 background_color 는 다른 화면(상세, 메인 카드 등) 에서 그대로 쓰이므로 별도 컬럼으로 추가한다.
-- 카테고리 그룹 분류는 text_color 기준과 동일하다 (같은 text_color → 같은 badge_background_color).
ALTER TABLE bill_categories
    ADD COLUMN badge_background_color VARCHAR(6);

UPDATE bill_categories
SET badge_background_color = CASE code
    WHEN 'BROADCAST'       THEN 'F8FFD2'
    WHEN 'WELFARE'         THEN 'F8FFD2'
    WHEN 'LABOR'           THEN 'F8FFD2'
    WHEN 'CHILD'           THEN 'F8FFD2'
    WHEN 'DIGITAL'         THEN 'D3DAFF'
    WHEN 'ECONOMY'         THEN 'D3DAFF'
    WHEN 'EDUCATION'       THEN 'D3DAFF'
    WHEN 'FOREIGN_DEFENSE' THEN 'D3DAFF'
    WHEN 'SECURITY'        THEN 'CDECCD'
    WHEN 'ENVIRONMENT'     THEN 'CDECCD'
    WHEN 'TRANSPORT'       THEN 'CDECCD'
    WHEN 'MEDICAL'         THEN 'CDECCD'
    WHEN 'FAMILY'          THEN 'CDECCD'
    WHEN 'REAL_ESTATE'     THEN 'FAE5E5'
    WHEN 'WOMEN'           THEN 'FAE5E5'
    WHEN 'SEX_CRIME'       THEN 'FAE5E5'
    WHEN 'LAW_ADMIN'       THEN 'FAE5E5'
END
WHERE code IN (
    'BROADCAST', 'WELFARE', 'LABOR', 'CHILD',
    'DIGITAL', 'ECONOMY', 'EDUCATION', 'FOREIGN_DEFENSE',
    'SECURITY', 'ENVIRONMENT', 'TRANSPORT', 'MEDICAL', 'FAMILY',
    'REAL_ESTATE', 'WOMEN', 'SEX_CRIME', 'LAW_ADMIN'
);

ALTER TABLE bill_categories
    ALTER COLUMN badge_background_color SET NOT NULL;
