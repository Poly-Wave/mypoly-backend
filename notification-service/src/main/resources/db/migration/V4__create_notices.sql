SET search_path TO notification_service;

-- 공지사항 게시판. 관리자는 admin-tool 범용 CRUD로 직접 등록/수정한다(별도 쓰기 API 없음).
-- broadcast_at 은 NoticeBroadcastScheduler 가 전체 유저에게 알림함 발급을 마친 뒤 채운다.
CREATE TABLE notices (
    id           BIGSERIAL PRIMARY KEY,
    title        VARCHAR(200) NOT NULL,
    content      TEXT NOT NULL,
    is_visible   BOOLEAN NOT NULL DEFAULT TRUE,
    broadcast_at TIMESTAMPTZ,
    created_at   TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at   TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_notices_visible_created ON notices (is_visible, created_at DESC);
CREATE INDEX idx_notices_broadcast_pending ON notices (broadcast_at) WHERE broadcast_at IS NULL;
