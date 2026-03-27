--liquibase formatted sql
--changeset JehadHamid:009-create-product-media-audit

CREATE TABLE IF NOT EXISTS audit.product_media_audit
(
    rev        INT NOT NULL,
    revtype    SMALLINT,
    id         BIGINT,
    product_id BIGINT,
    variant_id BIGINT,
    medium_id   uuid,
    sort_order INT,
    created_at timestamp,
    created_by VARCHAR(50),
    updated_at timestamp,
    updated_by VARCHAR(50),
    PRIMARY KEY (id, rev),
    CONSTRAINT fk_product_media_audit_rev
        FOREIGN KEY (rev)
            REFERENCES audit.revinfo (rev)
)
