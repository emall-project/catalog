-- liquibase formatted sql
-- changeset JehadHamid:012-create-category-audience-configs-table runOnChange:true

CREATE SEQUENCE IF NOT EXISTS catalog.category_audience_configs_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 10;

CREATE TABLE IF NOT EXISTS catalog.category_audience_configs
(
    id                BIGINT PRIMARY KEY DEFAULT nextval('catalog.category_audience_configs_seq'),
    category_id       BIGINT  NOT NULL,
    age_group         VARCHAR NOT NULL,
    targeted_audience VARCHAR NOT NULL,
    image_id          uuid    NOT NULL,
    FOREIGN KEY (category_id) REFERENCES catalog.categories (id) ON DELETE CASCADE
)