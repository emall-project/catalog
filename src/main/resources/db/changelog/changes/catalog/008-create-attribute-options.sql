-- liquibase formatted sql
-- changeset JehadHamid:008-create-attribute-options-table runOnChange:true

CREATE SEQUENCE IF NOT EXISTS catalog.attribute_options_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 10;

CREATE TABLE IF NOT EXISTS catalog.attribute_options
(
    id           BIGINT PRIMARY KEY   DEFAULT nextval('catalog.attribute_options_seq'),
    value        VARCHAR(50) NOT NULL,
    sort_order   INT         NOT NULL,
    attribute_id BIGINT      NOT NULL,
    created_at   timestamp   NOT NULL DEFAULT now(),
    created_by   VARCHAR(50) NOT NULL,
    updated_at   timestamp            DEFAULT now(),
    updated_by   VARCHAR(50),
    UNIQUE (value, attribute_id)
);

CREATE INDEX IF NOT EXISTS idx_option_attribute_id
    ON catalog.attribute_options (attribute_id);
