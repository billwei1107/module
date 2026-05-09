CREATE TABLE ann_announcements (
    id UUID PRIMARY KEY,
    title VARCHAR(160) NOT NULL,
    content VARCHAR(4000) NOT NULL,
    category VARCHAR(80),
    attachment_url VARCHAR(500),
    publisher_id VARCHAR(64),
    scheduled_publish_at TIMESTAMP,
    scheduled_unpublish_at TIMESTAMP,
    requires_confirmation BOOLEAN NOT NULL DEFAULT FALSE,
    status VARCHAR(20) NOT NULL DEFAULT 'SCHEDULED',
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    deleted_at TIMESTAMP
);

CREATE INDEX idx_ann_status_schedule ON ann_announcements(status, scheduled_publish_at, scheduled_unpublish_at);

CREATE TABLE ann_targets (
    id UUID PRIMARY KEY,
    announcement_id UUID NOT NULL,
    target_type VARCHAR(30) NOT NULL,
    target_id VARCHAR(64),
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    deleted_at TIMESTAMP
);

CREATE INDEX idx_ann_targets_announcement ON ann_targets(announcement_id);
CREATE INDEX idx_ann_targets_scope ON ann_targets(target_type, target_id);

CREATE TABLE ann_reads (
    id UUID PRIMARY KEY,
    announcement_id UUID NOT NULL,
    user_id VARCHAR(64) NOT NULL,
    read_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    deleted_at TIMESTAMP
);

CREATE UNIQUE INDEX uk_ann_reads_user ON ann_reads(announcement_id, user_id) WHERE deleted_at IS NULL;

CREATE TABLE ann_confirmations (
    id UUID PRIMARY KEY,
    announcement_id UUID NOT NULL,
    user_id VARCHAR(64) NOT NULL,
    confirmed_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    deleted_at TIMESTAMP
);

CREATE UNIQUE INDEX uk_ann_confirmations_user ON ann_confirmations(announcement_id, user_id) WHERE deleted_at IS NULL;
