SET search_path TO bill_service;

-- 안건 탭 메타(칩) 관리 테이블
CREATE TABLE agenda_tabs (
    code          VARCHAR(50)  NOT NULL,
    label         VARCHAR(50)  NOT NULL,
    description   VARCHAR(200) NOT NULL DEFAULT '',
    display_order INTEGER      NOT NULL DEFAULT 0,
    is_active     BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at    TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at    TIMESTAMPTZ  NOT NULL DEFAULT now(),

    CONSTRAINT pk_agenda_tabs PRIMARY KEY (code)
);

CREATE INDEX idx_agenda_tabs_active_order ON agenda_tabs (is_active, display_order);

COMMENT ON TABLE agenda_tabs IS '안건 탭(칩) 메타 정보 (라벨/설명/노출순서/활성화)';
COMMENT ON COLUMN agenda_tabs.code IS '탭 코드 (hot_debate, trending, personalized 등)';
COMMENT ON COLUMN agenda_tabs.label IS '탭 라벨';
COMMENT ON COLUMN agenda_tabs.description IS '탭 설명';
COMMENT ON COLUMN agenda_tabs.display_order IS '표시 순서(작을수록 앞)';
COMMENT ON COLUMN agenda_tabs.is_active IS '활성 여부';

-- 기본 탭 seed
INSERT INTO agenda_tabs (code, label, description, display_order, is_active)
VALUES
    ('hot_debate', '쟁쟁한', '찬반이 팽팽한 의안', 1, TRUE),
    ('personalized', '맞춤형', '내 관심사 기반 추천', 2, FALSE),
    ('trending', '요즘 핫한', '최근 급상승 의안', 3, TRUE);

