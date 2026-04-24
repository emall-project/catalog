-- liquibase formatted sql
-- changeset JehadHamid:014-create-favorites-table runOnChange:true

CREATE SEQUENCE IF NOT EXISTS catalog.favorites_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 10;

CREATE TABLE IF NOT EXISTS catalog.favorites
(
    id         BIGINT PRIMARY KEY DEFAULT nextval('catalog.favorites_seq'),
    product_id BIGINT      NOT NULL,
    "user"     VARCHAR(50) NOT NULL,
    added_at   TIMESTAMP   NOT NULL DEFAULT now(),

    CONSTRAINT fk_favorites_product
        FOREIGN KEY (product_id)
            REFERENCES catalog.products (id)
            ON DELETE CASCADE,

    CONSTRAINT uk_favorites_user_product
        UNIQUE ("user", product_id)
);

CREATE INDEX IF NOT EXISTS idx_favorites_user
    ON catalog.favorites ("user");

CREATE INDEX IF NOT EXISTS idx_favorites_product
    ON catalog.favorites (product_id);