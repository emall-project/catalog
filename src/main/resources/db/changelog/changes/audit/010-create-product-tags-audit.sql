--liquibase formatted sql
--changeset JehadHamid:010-create-product-tags-audit


CREATE TABLE IF NOT EXISTS audit.product_tags_audit
(
    rev        INT NOT NULL,
    revtype    SMALLINT,
    product_id BIGINT,
    tag_id     BIGINT,
    PRIMARY KEY (product_id, tag_id, rev),
    CONSTRAINT fk_product_tags_audit_rev
        FOREIGN KEY (rev)
            REFERENCES audit.revinfo (rev)
)