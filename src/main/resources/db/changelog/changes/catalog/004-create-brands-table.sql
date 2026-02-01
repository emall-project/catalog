-- liquibase formatted sql
-- changeset JehadHamid:004-create-brands-table runOnChange:true

CREATE SEQUENCE IF NOT EXISTS catalog.brands_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 10;

CREATE TABLE IF NOT EXISTS catalog.brands(
        id INT PRIMARY KEY DEFAULT nextval('catalog.brands_seq'),
        name VARCHAR NOT NULL,
        slug VARCHAR UNIQUE NOT NULL,
        created_at timestamp NOT NULL DEFAULT now(),
        created_by INT NOT NULL,
        updated_at  timestamp DEFAULT now(),
        updated_by INT
);
