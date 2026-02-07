-- liquibase formatted sql
-- changeset JehadHamid:009-create-products-variants-table runOnChange:true

CREATE SEQUENCE IF NOT EXISTS catalog.product_images_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 10;

CREATE TABLE IF NOT EXISTS catalog.product_images(
        id BIGINT PRIMARY KEY DEFAULT nextval('catalog.product_images_seq'),
        product_id BIGINT,
        variant_id BIGINT,
        sort_order SMALLINT,
        image_file_key uuid,
        created_at timestamp NOT NULL DEFAULT now(),
        created_by VARCHAR(50) NOT NULL,
        updated_at timestamp DEFAULT now(),
        updated_by VARCHAR(50),
        FOREIGN KEY (product_id) REFERENCES catalog.products(id)
);

