--liquibase formatted sql
--changeset JehadHamid:006-create-attribute-options-audit

CREATE TABLE IF NOT EXISTS audit.attribute_options_audit
(
    rev          INT NOT NULL,
    revtype      SMALLINT,
    id           BIGINT,
    value        VARCHAR(50),
    sort_order   INT,
    attribute_id BIGINT,
    created_at   timestamp,
    created_by   VARCHAR(50),
    updated_at   timestamp,
    updated_by   VARCHAR(50),
    PRIMARY KEY (id, rev),
    CONSTRAINT fk_attribute_options_audit_rev
        FOREIGN KEY (rev)
            REFERENCES audit.revinfo (rev)
)
