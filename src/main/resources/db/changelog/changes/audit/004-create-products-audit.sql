--liquibase formatted sql
--changeset JehadHamid:004-create-products-audit

CREATE TABLE IF NOT EXISTS audit.products_audit
(
    rev               INT NOT NULL,
    revtype           SMALLINT,
    id                BIGINT,
    name              VARCHAR(50),
    slug              VARCHAR(50),
    targeted_audience VARCHAR(20),
    age_group         VARCHAR(20),
    is_active         BOOLEAN,
    short_description VARCHAR(100),
    description       TEXT,
    brand_id          BIGINT,
    category_id       BIGINT,
    mall_id           BIGINT,
    store_id          BIGINT,
    created_at        timestamp,
    created_by        VARCHAR(50),
    updated_at        timestamp,
    updated_by        VARCHAR(50),
    PRIMARY KEY (id, rev),
    CONSTRAINT fk_products_audit_rev
        FOREIGN KEY (rev)
            REFERENCES audit.revinfo (rev)
)
