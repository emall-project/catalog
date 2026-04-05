--liquibase formatted sql
--changeset lamahafiz:003-create-comment-moderation-log

CREATE SEQUENCE IF NOT EXISTS catalog.comment_moderation_log_seq
    START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1;

CREATE TABLE IF NOT EXISTS catalog.comment_moderation_log (
    log_id BIGINT PRIMARY KEY DEFAULT nextval('catalog.comment_moderation_log_seq'),
    comment_id BIGINT NOT NULL,
    provider VARCHAR(20) NOT NULL,
    decision VARCHAR(20) NOT NULL,
    reason TEXT,
    confidence NUMERIC(5, 4),
    raw_response TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_log_comment FOREIGN KEY (comment_id)
        REFERENCES catalog.product_comments (comment_id) ON DELETE CASCADE
);


