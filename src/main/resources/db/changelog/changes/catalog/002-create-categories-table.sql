-- liquibase formatted sql
-- changeset JehadHamid:002-create-categories-table runOnChange:true

CREATE SEQUENCE IF NOT EXISTS catalog.categories_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 10;

CREATE TABLE IF NOT EXISTS catalog.categories(
        id INT PRIMARY KEY DEFAULT nextval('catalog.categories_seq'),
        name VARCHAR NOT NULL,
        slug VARCHAR UNIQUE NOT NULL,
        parent_id INT,
        created_at timestamp NOT NULL DEFAULT now(),
        created_by INT,
        updated_at  timestamp DEFAULT now(),
        updated_by INT,
        FOREIGN KEY (parent_id) REFERENCES catalog.categories(id)
);
