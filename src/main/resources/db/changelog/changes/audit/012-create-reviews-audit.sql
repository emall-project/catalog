--liquibase formatted sql
--changeset lamahafiz:012-create-reviews-audit

CREATE TABLE IF NOT EXISTS audit.product_reviews_audit (
    rev         INT         NOT NULL,
    revtype     SMALLINT,
    review_id   BIGINT      NOT NULL,
    product_id  BIGINT,
    user_id     BIGINT,
    rating      SMALLINT,
    created_at  TIMESTAMP,
    created_by  VARCHAR(255),
    updated_at  TIMESTAMP,
    updated_by  VARCHAR(255),

    PRIMARY KEY (review_id, rev),
    CONSTRAINT fk_reviews_audit_rev FOREIGN KEY (rev) REFERENCES audit.revinfo (rev)
);