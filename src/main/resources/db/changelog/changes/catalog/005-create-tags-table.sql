-- liquibase formatted sql
-- changeset JehadHamid:005-create-tags-table runOnChange:true

CREATE SEQUENCE IF NOT EXISTS catalog.tags_sequence
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 10;

CREATE TABLE IF NOT EXISTS catalog.tags
(
    id         BIGINT PRIMARY KEY          DEFAULT nextval('catalog.tags_sequence'),
    name       VARCHAR(50) UNIQUE NOT NULL,
    created_at timestamp          NOT NULL DEFAULT now(),
    created_by VARCHAR(50)        NOT NULL,
    updated_at timestamp                   DEFAULT now(),
    updated_by VARCHAR(50)
)