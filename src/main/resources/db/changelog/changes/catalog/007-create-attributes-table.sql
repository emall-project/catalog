-- liquibase formatted sql
-- changeset JehadHamid:007-create-attributes-table runOnChange:true

CREATE SEQUENCE IF NOT EXISTS catalog.attributes_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 10;

CREATE TABLE IF NOT EXISTS catalog.attributes
(
    id         BIGINT PRIMARY KEY          DEFAULT nextval('catalog.attributes_seq'),
    name       VARCHAR(50) UNIQUE NOT NULL,
    slug       VARCHAR(50)        NOT NULL,
    type       VARCHAR(20)        NOT NULL,
    is_active  BOOLEAN            NOT NULL DEFAULT TRUE,
    created_at timestamp          NOT NULL DEFAULT now(),
    created_by VARCHAR(50)        NOT NULL,
    updated_at timestamp                   DEFAULT now(),
    updated_by VARCHAR(50)
)