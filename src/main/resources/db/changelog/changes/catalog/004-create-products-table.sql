-- liquibase formatted sql
-- changeset JehadHamid:004-create-products-table runOnChange:true

CREATE SEQUENCE IF NOT EXISTS catalog.products_sequence
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 10;

CREATE TABLE IF NOT EXISTS catalog.products
(
    id                BIGINT PRIMARY KEY    DEFAULT nextval('catalog.products_sequence'),
    name              VARCHAR(50)  NOT NULL,
    slug              VARCHAR(50)  NOT NULL,
    targeted_audience VARCHAR(20)  NOT NULL,
    age_group         VARCHAR(20)  NOT NULL,
    is_active         BOOLEAN      NOT NULL DEFAULT TRUE,
    short_description VARCHAR(100) NOT NULL,
    description       TEXT         NOT NULL,
    brand_id          BIGINT       NOT NULL,
    category_id       BIGINT       NOT NULL,
    mall_id           BIGINT       NOT NULL,
    store_id          BIGINT       NOT NULL,
    created_at        timestamp    NOT NULL DEFAULT now(),
    created_by        VARCHAR(50)  NOT NULL,
    updated_at        timestamp             DEFAULT now(),
    updated_by        VARCHAR(50),
    UNIQUE (slug, store_id),
    FOREIGN KEY (brand_id) REFERENCES catalog.brands (id),
    FOREIGN KEY (category_id) REFERENCES catalog.categories (id)
);

CREATE INDEX IF NOT EXISTS idx_product_mall_category_target_age
    ON catalog.products (mall_id, category_id, targeted_audience, age_group);

CREATE INDEX IF NOT EXISTS idx_product_mall_store_target_age
    ON catalog.products (mall_id, store_id, targeted_audience, age_group);

CREATE INDEX IF NOT EXISTS idx_product_mall_brand_target_age
    ON catalog.products (mall_id, brand_id, targeted_audience, age_group);

