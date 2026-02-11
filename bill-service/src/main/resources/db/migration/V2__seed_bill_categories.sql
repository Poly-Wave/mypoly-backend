-- bill-service seed categories (mobile UI order)
-- NOTE: 화면(3열 그리드) 기준: 좌->우 / 위->아래 순서

SET search_path TO bill_service;

INSERT INTO bill_categories (code, name, display_order, is_active)
VALUES
    ('DIGITAL', '디지털', 1, TRUE),
    ('SECURITY', '보안', 2, TRUE),
    ('BROADCAST', '방통', 3, TRUE),

    ('ECONOMY', '경제', 4, TRUE),
    ('REAL_ESTATE', '부동산', 5, TRUE),
    ('TRANSPORT', '교통', 6, TRUE),

    ('ENVIRONMENT', '환경', 7, TRUE),
    ('MEDICAL', '의료', 8, TRUE),
    ('WELFARE', '복지', 9, TRUE),

    ('EDUCATION', '교육', 10, TRUE),
    ('LABOR', '노동', 11, TRUE),
    ('WOMEN', '여성', 12, TRUE),

    ('FAMILY', '가족', 13, TRUE),
    ('CHILD', '아동', 14, TRUE),
    ('SEX_CRIME', '성범죄', 15, TRUE),

    ('FOREIGN_DEFENSE', '외교안보', 16, TRUE),
    ('LAW_ADMIN', '법·행정', 17, TRUE)
ON CONFLICT (code) DO NOTHING;
