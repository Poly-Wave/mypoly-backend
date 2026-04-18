SET search_path TO bill_service;

ALTER TABLE bill_proposers
ADD COLUMN member_id BIGINT;

ALTER TABLE bill_proposers
ADD CONSTRAINT fk_bill_proposers_member
FOREIGN KEY (member_id)
REFERENCES bill_members(id)
ON DELETE SET NULL;

CREATE INDEX idx_bill_proposers_member_id
ON bill_proposers (member_id);

-- 기존 발의자 데이터 중 의원명이 bill_members에서 유일하게 매칭되는 경우만 안전하게 backfill 한다.
-- 동명이인 가능성이 있는 이름은 잘못 연결하지 않기 위해 제외한다.
WITH unique_members AS (
    SELECT
        name,
        MIN(id) AS member_id
    FROM bill_members
    WHERE name IS NOT NULL
      AND btrim(name) <> ''
    GROUP BY name
    HAVING COUNT(*) = 1
)
UPDATE bill_proposers p
SET member_id = um.member_id
FROM unique_members um
WHERE p.member_id IS NULL
  AND btrim(p.proposer_name) = btrim(um.name);