--liquibase formatted sql
--changeset JehadHamid:002-create-favorites-audit

CREATE TABLE IF NOT EXISTS audit.favorites_audit
(
    rev        INT NOT NULL,
    revtype    SMALLINT,
    id         BIGINT,
    product_id BIGINT,
    "user"     VARCHAR(50),
    added_at   TIMESTAMP,
    PRIMARY KEY (id, rev),
    CONSTRAINT fk_favorites_audit_rev
        FOREIGN KEY (rev)
            REFERENCES audit.revinfo (rev)
)