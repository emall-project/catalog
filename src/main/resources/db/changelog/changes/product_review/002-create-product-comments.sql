--liquibase formatted sql
--changeset lamahafiz:002-create-product-comments

CREATE SEQUENCE IF NOT EXISTS catalog.product_comments_seq
    START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1;

CREATE TABLE IF NOT EXISTS catalog.product_comments (
    comment_id BIGINT PRIMARY KEY DEFAULT nextval('catalog.product_comments_seq'),
    product_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    content TEXT NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'PENDING_MODERATION',
    rejection_reason TEXT,
    product_url TEXT,
    moderation_retry_count INT NOT NULL DEFAULT 0,
    last_moderation_attempt_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_at TIMESTAMP,
    updated_by VARCHAR(255),

    CONSTRAINT uk_comment_user_product UNIQUE (product_id, user_id),
    CONSTRAINT fk_comment_product FOREIGN KEY (product_id)
        REFERENCES catalog.products (id) ON DELETE CASCADE
);