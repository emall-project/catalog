-- liquibase formatted sql
-- changeset JehadHamid:009-create-product-variants-table runOnChange:true

CREATE SEQUENCE IF NOT EXISTS catalog.product_variants_sequence
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 10;

CREATE TABLE IF NOT EXISTS catalog.product_variants
(
    id         BIGINT PRIMARY KEY   DEFAULT nextval('catalog.product_variants_sequence'),
    product_id BIGINT      NOT NULL,
    name       VARCHAR(50) NOT NULL,
    base_price DECIMAL     NOT NULL,
    is_default BOOLEAN     NOT NULL DEFAULT false,
    created_at timestamp   NOT NULL DEFAULT now(),
    created_by VARCHAR(50) NOT NULL,
    updated_at timestamp            DEFAULT now(),
    updated_by VARCHAR(50),
    FOREIGN KEY (product_id) REFERENCES catalog.products (id)
);