--liquibase formatted sql
--changeset lamahafiz:015-add-comment-moderation-retry-and-rejection-columns-audit

ALTER TABLE audit.product_comments_audit
    ADD COLUMN IF NOT EXISTS rejection_reason TEXT,
    ADD COLUMN IF NOT EXISTS moderation_retry_count INT,
    ADD COLUMN IF NOT EXISTS last_moderation_attempt_at TIMESTAMP,
    ADD COLUMN IF NOT EXISTS product_url TEXT;
