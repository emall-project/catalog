-- liquibase formatted sql
-- changeset JehadHamid:002-create-categories-table runOnChange:true

CREATE SEQUENCE IF NOT EXISTS catalog.categories_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 10;

CREATE TABLE IF NOT EXISTS catalog.categories(
        id BIGINT PRIMARY KEY DEFAULT nextval('catalog.categories_seq'),
        name VARCHAR(50) NOT NULL,
        slug VARCHAR(50) UNIQUE NOT NULL,
        is_active BOOLEAN DEFAULT TRUE,
        image_file_key UUID,
        parent_id BIGINT,
        created_at timestamp NOT NULL DEFAULT now(),
        created_by VARCHAR(50) NOT NULL,
        updated_at  timestamp DEFAULT now(),
        updated_by VARCHAR(50),
        FOREIGN KEY (parent_id) REFERENCES catalog.categories(id)
);
