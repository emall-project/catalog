-- liquibase formatted sql
-- changeset JehadHamid:006-create-product-tags-table runOnChange:true

CREATE TABLE IF NOT EXISTS catalog.product_tags
(
    product_id BIGINT NOT NULL,
    tag_id     BIGINT NOT NULL,
    PRIMARY KEY (product_id, tag_id)

)