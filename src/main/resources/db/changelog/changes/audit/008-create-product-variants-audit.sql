--liquibase formatted sql
--changeset JehadHamid:006-create-product-variants-audit

CREATE TABLE IF NOT EXISTS audit.product_variants_audit
(
    rev        INT NOT NULL,
    revtype    SMALLINT,
    id         BIGINT,
    name       VARCHAR(50),
    product_id BIGINT,
    base_price DECIMAL,
    is_default BOOLEAN,
    created_at timestamp,
    created_by VARCHAR(50),
    updated_at timestamp,
    updated_by VARCHAR(50),
    PRIMARY KEY (id, rev),
    CONSTRAINT fk_product_variants_audit_rev
        FOREIGN KEY (rev)
            REFERENCES audit.revinfo (rev)
)
