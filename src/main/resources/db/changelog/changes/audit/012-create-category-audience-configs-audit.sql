-- liquibase formatted sql
-- changeset JehadHamid:012-create-category-audience-configs-audit

CREATE TABLE IF NOT EXISTS audit.category_audience_configs_audit
(
    rev               INT NOT NULL,
    revtype           SMALLINT,
    id                BIGINT,
    category_id       BIGINT,
    age_group         VARCHAR,
    targeted_audience VARCHAR,
    image_id          uuid,
    PRIMARY KEY (id, rev),
    CONSTRAINT fk_variant_attribute_audit_rev
        FOREIGN KEY (rev)
            REFERENCES audit.revinfo (rev)
)