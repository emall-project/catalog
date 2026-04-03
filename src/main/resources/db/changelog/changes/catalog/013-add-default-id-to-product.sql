-- liquibase formatted sql
-- changeset JehadHamid:013-add-default-id-to-product runOnChange:true

ALTER TABLE catalog.products
    ADD COLUMN default_id BIGINT,
    ADD CONSTRAINT fk_products_default_variant
        FOREIGN KEY (default_id)
            REFERENCES catalog.product_variants (id);