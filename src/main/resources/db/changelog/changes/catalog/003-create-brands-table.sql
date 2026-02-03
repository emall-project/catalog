-- liquibase formatted sql
-- changeset JehadHamid:003-create-brands-table runOnChange:true

CREATE SEQUENCE IF NOT EXISTS catalog.brands_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 10;

CREATE TABLE IF NOT EXISTS catalog.brands(
        id BIGINT PRIMARY KEY DEFAULT nextval('catalog.brands_seq'),
        name VARCHAR(50) NOT NULL,
        slug VARCHAR(50) UNIQUE NOT NULL,
        is_active BOOLEAN DEFAULT TRUE,
        image_file_key UUID,
        created_at timestamp NOT NULL DEFAULT now(),
        created_by VARCHAR(50) NOT NULL,
        updated_at  timestamp DEFAULT now(),
        updated_by VARCHAR(50)
);
