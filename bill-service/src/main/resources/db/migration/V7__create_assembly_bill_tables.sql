SET search_path TO bill_service;

-- 1. assembly_bills
CREATE TABLE assembly_bills (
    id                          BIGSERIAL PRIMARY KEY,
    external_bill_id            VARCHAR(100) NOT NULL,
    bill_no                     VARCHAR(50),
    official_title              VARCHAR(500) NOT NULL,
    proposal_date               DATE,
    proposer_kind               VARCHAR(50),
    representative_proposer_name VARCHAR(100),
    proposer_count              INTEGER NOT NULL DEFAULT 0,

    summary_raw                 TEXT,
    summary_raw_hash            VARCHAR(64),
    detail_url                  VARCHAR(500),

    current_proc_stage_code     VARCHAR(50),
    current_proc_stage_name     VARCHAR(100),
    current_proc_stage_order    INTEGER,
    current_pass_gubn           VARCHAR(50),
    current_general_result      VARCHAR(500),

    source_payload              JSONB,

    first_collected_at          TIMESTAMPTZ NOT NULL DEFAULT now(),
    last_collected_at           TIMESTAMPTZ NOT NULL DEFAULT now(),
    last_status_changed_at      TIMESTAMPTZ,

    created_at                  TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at                  TIMESTAMPTZ NOT NULL DEFAULT now(),

    CONSTRAINT uk_assembly_bills_external_bill_id UNIQUE (external_bill_id)
);

CREATE INDEX idx_assembly_bills_bill_no ON assembly_bills (bill_no);
CREATE INDEX idx_assembly_bills_proposal_date ON assembly_bills (proposal_date DESC);
CREATE INDEX idx_assembly_bills_stage_order ON assembly_bills (current_proc_stage_order);
CREATE INDEX idx_assembly_bills_pass_gubn ON assembly_bills (current_pass_gubn);
CREATE INDEX idx_assembly_bills_last_collected_at ON assembly_bills (last_collected_at DESC);

-- 2. assembly_bill_proposers
CREATE TABLE assembly_bill_proposers (
    id                  BIGSERIAL PRIMARY KEY,
    bill_id             BIGINT NOT NULL,
    proposer_name       VARCHAR(100) NOT NULL,
    proposer_type       VARCHAR(50),
    is_representative   BOOLEAN NOT NULL DEFAULT FALSE,
    display_order       INTEGER NOT NULL DEFAULT 0,
    source_payload      JSONB,
    created_at          TIMESTAMPTZ NOT NULL DEFAULT now(),

    CONSTRAINT fk_assembly_bill_proposers_bill
        FOREIGN KEY (bill_id) REFERENCES assembly_bills(id) ON DELETE CASCADE
);

CREATE INDEX idx_assembly_bill_proposers_bill_id ON assembly_bill_proposers (bill_id);
CREATE INDEX idx_assembly_bill_proposers_name ON assembly_bill_proposers (proposer_name);

-- 3. assembly_bill_status_history
CREATE TABLE assembly_bill_status_history (
    id                  BIGSERIAL PRIMARY KEY,
    bill_id             BIGINT NOT NULL,
    proc_stage_code     VARCHAR(50),
    proc_stage_name     VARCHAR(100),
    proc_stage_order    INTEGER,
    pass_gubn           VARCHAR(50),
    general_result      VARCHAR(500),
    proc_date           DATE,
    observed_at         TIMESTAMPTZ NOT NULL DEFAULT now(),
    status_payload      JSONB,

    CONSTRAINT fk_assembly_bill_status_history_bill
        FOREIGN KEY (bill_id) REFERENCES assembly_bills(id) ON DELETE CASCADE
);

CREATE INDEX idx_assembly_bill_status_history_bill_id ON assembly_bill_status_history (bill_id);
CREATE INDEX idx_assembly_bill_status_history_proc_date ON assembly_bill_status_history (proc_date DESC);
CREATE INDEX idx_assembly_bill_status_history_observed_at ON assembly_bill_status_history (observed_at DESC);

-- 같은 상태를 과도하게 중복 적재하지 않도록 최소 보호
CREATE UNIQUE INDEX uk_assembly_bill_status_history_dedup
ON assembly_bill_status_history (
    bill_id,
    COALESCE(proc_stage_code, ''),
    COALESCE(pass_gubn, ''),
    COALESCE(general_result, ''),
    COALESCE(proc_date, DATE '1900-01-01')
);

-- 4. assembly_members
CREATE TABLE assembly_members (
    id                      BIGSERIAL PRIMARY KEY,
    external_member_id      VARCHAR(100) NOT NULL,
    mona_cd                 VARCHAR(50),
    member_no               VARCHAR(50),

    name                    VARCHAR(100) NOT NULL,
    name_chinese            VARCHAR(100),
    name_english            VARCHAR(200),

    party_name              VARCHAR(100),
    district_name           VARCHAR(200),
    district_type           VARCHAR(100),

    committee_name          VARCHAR(500),
    current_committee_name  VARCHAR(200),

    era                     VARCHAR(100),
    election_type           VARCHAR(50),
    gender                  VARCHAR(10),
    birth_date              DATE,

    photo_url               VARCHAR(500),
    homepage_url            VARCHAR(500),
    brief_history           TEXT,

    source_payload          JSONB,

    created_at              TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at              TIMESTAMPTZ NOT NULL DEFAULT now(),

    CONSTRAINT uk_assembly_members_external_member_id UNIQUE (external_member_id)
);

CREATE INDEX idx_assembly_members_name ON assembly_members (name);
CREATE INDEX idx_assembly_members_party_name ON assembly_members (party_name);
CREATE INDEX idx_assembly_members_mona_cd ON assembly_members (mona_cd);
CREATE INDEX idx_assembly_members_member_no ON assembly_members (member_no);

-- 5. assembly_bill_votes
CREATE TABLE assembly_bill_votes (
    id                      BIGSERIAL PRIMARY KEY,
    bill_id                 BIGINT NOT NULL,
    member_id               BIGINT,
    external_vote_key       VARCHAR(200) NOT NULL,

    member_name             VARCHAR(100),
    member_no               VARCHAR(50),
    mona_cd                 VARCHAR(50),

    party_name_snapshot     VARCHAR(100),
    district_name_snapshot  VARCHAR(200),
    committee_name_snapshot VARCHAR(200),

    vote_result             VARCHAR(50) NOT NULL,
    vote_date               TIMESTAMPTZ,

    source_payload          JSONB,
    created_at              TIMESTAMPTZ NOT NULL DEFAULT now(),

    CONSTRAINT fk_assembly_bill_votes_bill
        FOREIGN KEY (bill_id) REFERENCES assembly_bills(id) ON DELETE CASCADE,
    CONSTRAINT fk_assembly_bill_votes_member
        FOREIGN KEY (member_id) REFERENCES assembly_members(id) ON DELETE SET NULL,
    CONSTRAINT uk_assembly_bill_votes_external_vote_key UNIQUE (external_vote_key)
);

CREATE INDEX idx_assembly_bill_votes_bill_id ON assembly_bill_votes (bill_id);
CREATE INDEX idx_assembly_bill_votes_member_id ON assembly_bill_votes (member_id);
CREATE INDEX idx_assembly_bill_votes_vote_result ON assembly_bill_votes (vote_result);
CREATE INDEX idx_assembly_bill_votes_vote_date ON assembly_bill_votes (vote_date DESC);

-- 6. assembly_bill_ai_analyses
CREATE TABLE assembly_bill_ai_analyses (
    id                  BIGSERIAL PRIMARY KEY,
    bill_id             BIGINT NOT NULL,
    analysis_version    INTEGER NOT NULL,
    analysis_status     VARCHAR(20) NOT NULL,
    headline            VARCHAR(300),
    summary             TEXT,
    source_text_hash    VARCHAR(64),
    model_name          VARCHAR(100),
    prompt_version      VARCHAR(50),
    temperature         NUMERIC(4,2),
    analysis_input      JSONB,
    analysis_output     JSONB,
    error_message       TEXT,
    is_current          BOOLEAN NOT NULL DEFAULT FALSE,
    generated_at        TIMESTAMPTZ NOT NULL DEFAULT now(),
    created_at          TIMESTAMPTZ NOT NULL DEFAULT now(),

    CONSTRAINT fk_assembly_bill_ai_analyses_bill
        FOREIGN KEY (bill_id) REFERENCES assembly_bills(id) ON DELETE CASCADE,
    CONSTRAINT uk_assembly_bill_ai_analyses_bill_version UNIQUE (bill_id, analysis_version),
    CONSTRAINT ck_assembly_bill_ai_analyses_status
        CHECK (analysis_status IN ('SUCCESS', 'FAILED', 'SKIPPED'))
);

CREATE INDEX idx_assembly_bill_ai_analyses_bill_id ON assembly_bill_ai_analyses (bill_id);
CREATE INDEX idx_assembly_bill_ai_analyses_is_current ON assembly_bill_ai_analyses (bill_id, is_current);
CREATE INDEX idx_assembly_bill_ai_analyses_generated_at ON assembly_bill_ai_analyses (generated_at DESC);

-- bill 당 current 분석은 최대 1개
CREATE UNIQUE INDEX uk_assembly_bill_ai_analyses_current_per_bill
ON assembly_bill_ai_analyses (bill_id)
WHERE is_current = TRUE;

-- 7. assembly_bill_ai_categories
CREATE TABLE assembly_bill_ai_categories (
    id              BIGSERIAL PRIMARY KEY,
    analysis_id     BIGINT NOT NULL,
    category_id     BIGINT NOT NULL,
    rank_order      INTEGER NOT NULL DEFAULT 1,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),

    CONSTRAINT fk_assembly_bill_ai_categories_analysis
        FOREIGN KEY (analysis_id) REFERENCES assembly_bill_ai_analyses(id) ON DELETE CASCADE,
    CONSTRAINT fk_assembly_bill_ai_categories_category
        FOREIGN KEY (category_id) REFERENCES bill_categories(id) ON DELETE RESTRICT,
    CONSTRAINT uk_assembly_bill_ai_categories_analysis_category UNIQUE (analysis_id, category_id),
    CONSTRAINT uk_assembly_bill_ai_categories_analysis_rank UNIQUE (analysis_id, rank_order)
);

CREATE INDEX idx_assembly_bill_ai_categories_analysis_id ON assembly_bill_ai_categories (analysis_id);
CREATE INDEX idx_assembly_bill_ai_categories_category_id ON assembly_bill_ai_categories (category_id);

-- 8. assembly_bill_ai_axis_weights
CREATE TABLE assembly_bill_ai_axis_weights (
    id              BIGSERIAL PRIMARY KEY,
    analysis_id     BIGINT NOT NULL,
    opinion_type    VARCHAR(20) NOT NULL,
    axis_code       VARCHAR(2) NOT NULL,
    weight          INTEGER NOT NULL DEFAULT 1,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),

    CONSTRAINT fk_assembly_bill_ai_axis_weights_analysis
        FOREIGN KEY (analysis_id) REFERENCES assembly_bill_ai_analyses(id) ON DELETE CASCADE,
    CONSTRAINT uk_assembly_bill_ai_axis_weights_unique UNIQUE (analysis_id, opinion_type, axis_code),
    CONSTRAINT ck_assembly_bill_ai_axis_weights_opinion_type
        CHECK (opinion_type IN ('FOR', 'AGAINST')),
    CONSTRAINT ck_assembly_bill_ai_axis_weights_axis_code
        CHECK (axis_code IN ('P', 'M', 'U', 'T', 'N', 'S', 'O', 'R')),
    CONSTRAINT ck_assembly_bill_ai_axis_weights_weight
        CHECK (weight > 0)
);

CREATE INDEX idx_assembly_bill_ai_axis_weights_analysis_id ON assembly_bill_ai_axis_weights (analysis_id);

-- 9. assembly_batch_runs
CREATE TABLE assembly_batch_runs (
    id                  BIGSERIAL PRIMARY KEY,
    job_type            VARCHAR(50) NOT NULL,
    trigger_type        VARCHAR(20) NOT NULL,
    run_status          VARCHAR(20) NOT NULL,

    started_at          TIMESTAMPTZ NOT NULL DEFAULT now(),
    finished_at         TIMESTAMPTZ,

    image_tag           VARCHAR(100),
    git_commit_sha      VARCHAR(64),

    records_found       INTEGER NOT NULL DEFAULT 0,
    records_inserted    INTEGER NOT NULL DEFAULT 0,
    records_updated     INTEGER NOT NULL DEFAULT 0,
    records_skipped     INTEGER NOT NULL DEFAULT 0,
    records_failed      INTEGER NOT NULL DEFAULT 0,

    message             TEXT,
    created_at          TIMESTAMPTZ NOT NULL DEFAULT now(),

    CONSTRAINT ck_assembly_batch_runs_trigger_type
        CHECK (trigger_type IN ('CRON', 'MANUAL', 'RETRY')),
    CONSTRAINT ck_assembly_batch_runs_run_status
        CHECK (run_status IN ('RUNNING', 'SUCCESS', 'PARTIAL_SUCCESS', 'FAILED'))
);

CREATE INDEX idx_assembly_batch_runs_job_type ON assembly_batch_runs (job_type);
CREATE INDEX idx_assembly_batch_runs_run_status ON assembly_batch_runs (run_status);
CREATE INDEX idx_assembly_batch_runs_started_at ON assembly_batch_runs (started_at DESC);

-- 10. assembly_batch_run_items
CREATE TABLE assembly_batch_run_items (
    id                  BIGSERIAL PRIMARY KEY,
    batch_run_id        BIGINT NOT NULL,
    item_type           VARCHAR(30) NOT NULL,
    target_external_id  VARCHAR(200),
    target_internal_id  BIGINT,
    item_status         VARCHAR(20) NOT NULL,
    action_type         VARCHAR(20),
    error_message       TEXT,
    payload             JSONB,
    created_at          TIMESTAMPTZ NOT NULL DEFAULT now(),

    CONSTRAINT fk_assembly_batch_run_items_batch_run
        FOREIGN KEY (batch_run_id) REFERENCES assembly_batch_runs(id) ON DELETE CASCADE,
    CONSTRAINT ck_assembly_batch_run_items_item_status
        CHECK (item_status IN ('SUCCESS', 'SKIPPED', 'FAILED')),
    CONSTRAINT ck_assembly_batch_run_items_action_type
        CHECK (action_type IN ('INSERT', 'UPDATE', 'NO_CHANGE', 'DELETE'))
);

CREATE INDEX idx_assembly_batch_run_items_batch_run_id ON assembly_batch_run_items (batch_run_id);
CREATE INDEX idx_assembly_batch_run_items_item_type ON assembly_batch_run_items (item_type);
CREATE INDEX idx_assembly_batch_run_items_target_external_id ON assembly_batch_run_items (target_external_id);