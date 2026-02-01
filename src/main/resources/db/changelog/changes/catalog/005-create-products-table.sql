-- liquibase formatted sql
-- changeset JehadHamid:005-create-products-table runOnChange:true

CREATE SEQUENCE IF NOT EXISTS catalog.products_sequence
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 10;

CREATE TABLE IF NOT EXISTS catalog.products(
        id INT PRIMARY KEY DEFAULT nextval('catalog.products_sequence'),
        name VARCHAR NOT NULL,
        slug VARCHAR NOT NULL,
        short_description VARCHAR NOT NULL,
        description TEXT NOT NULL,
        brand_id INT,
        category_id INT,
        mall_id INT,
        store_id INT,
        created_at timestamp NOT NULL DEFAULT now(),
        created_by INT NOT NULL,
        updated_at  timestamp DEFAULT now(),
        updated_by INT,
        UNIQUE (slug, store_id),
        FOREIGN KEY (brand_id) REFERENCES catalog.brands(id),
        FOREIGN KEY (category_id) REFERENCES catalog.categories(id)
);

