-- liquibase formatted sql
-- changeset JehadHamid:11-create-variant-attributes-audit

CREATE TABLE IF NOT EXISTS audit.variant_attributes_audit
(
    rev          INT NOT NULL,
    revtype      SMALLINT,
    id           BIGINT,
    variant_id   BIGINT,
    attribute_id BIGINT,
    option_id    BIGINT,
    PRIMARY KEY (id, rev),
    CONSTRAINT fk_variant_attribute_audit_rev
        FOREIGN KEY (rev)
            REFERENCES audit.revinfo (rev)
)