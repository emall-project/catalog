--liquibase formatted sql
--changeset lamahafiz:001-create-product-reviews

CREATE SEQUENCE IF NOT EXISTS catalog.product_reviews_seq
    START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1;

CREATE TABLE IF NOT EXISTS catalog.product_reviews (
    review_id BIGINT PRIMARY KEY DEFAULT nextval('catalog.product_reviews_seq'),
    product_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    rating SMALLINT NOT NULL CHECK (rating BETWEEN 1 AND 5),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_at TIMESTAMP,
    updated_by VARCHAR(255),

    CONSTRAINT uk_review_user_product UNIQUE (product_id, user_id),
    CONSTRAINT fk_review_product FOREIGN KEY (product_id)
        REFERENCES catalog.products (id) ON DELETE CASCADE
);
