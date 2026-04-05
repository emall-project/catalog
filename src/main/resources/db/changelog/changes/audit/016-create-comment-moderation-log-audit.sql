--liquibase formatted sql
--changeset lamahafiz:016-create-comment-moderation-log-audit

CREATE TABLE IF NOT EXISTS audit.comment_moderation_log_audit (
    rev INT NOT NULL,
    revtype SMALLINT,
    log_id BIGINT NOT NULL,
    comment_id BIGINT,
    provider VARCHAR(20),
    decision VARCHAR(20),
    reason TEXT,
    confidence NUMERIC(5, 4),
    raw_response TEXT,
    created_at TIMESTAMP,

    PRIMARY KEY (log_id, rev),
    CONSTRAINT fk_moderation_log_audit_rev FOREIGN KEY (rev)
    REFERENCES audit.revinfo (rev)
);