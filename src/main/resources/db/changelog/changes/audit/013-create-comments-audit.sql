--liquibase formatted sql
--changeset lamahafiz:013-create-comments-audit

CREATE TABLE IF NOT EXISTS audit.product_comments_audit (
    rev         INT         NOT NULL,
    revtype     SMALLINT,
    comment_id  BIGINT      NOT NULL,
    product_id  BIGINT,
    user_id     BIGINT,
    content     TEXT,
    status      VARCHAR(30),
    created_at  TIMESTAMP,
    created_by  VARCHAR(255),
    updated_at  TIMESTAMP,
    updated_by  VARCHAR(255),

    PRIMARY KEY (comment_id, rev),
    CONSTRAINT fk_comments_audit_rev FOREIGN KEY (rev) REFERENCES audit.revinfo (rev)
);