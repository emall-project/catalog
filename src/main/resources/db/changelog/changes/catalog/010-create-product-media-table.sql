-- liquibase formatted sql
-- changeset JehadHamid:010-create-products-media-table runOnChange:true

CREATE SEQUENCE IF NOT EXISTS catalog.product_media_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 10;

CREATE TABLE IF NOT EXISTS catalog.product_media
(
    id         BIGINT PRIMARY KEY   DEFAULT nextval('catalog.product_media_seq'),
    product_id BIGINT      NOT NULL,
    variant_id BIGINT      NOT NULL,
    medium_id  uuid        NOT NULL,
    sort_order INT         NOT NULL,
    created_at timestamp   NOT NULL DEFAULT now(),
    created_by VARCHAR(50) NOT NULL,
    updated_at timestamp            DEFAULT now(),
    updated_by VARCHAR(50),
    FOREIGN KEY (product_id) REFERENCES catalog.products (id) ON DELETE CASCADE,
    FOREIGN KEY (variant_id) REFERENCES catalog.product_variants (id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_product_media_variant
    ON catalog.product_media (variant_id);
