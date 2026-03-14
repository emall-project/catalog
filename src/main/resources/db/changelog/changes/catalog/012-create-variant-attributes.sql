-- liquibase formatted sql
-- changeset JehadHamid:11-create-variant-attributes-table runOnChange:true

CREATE SEQUENCE IF NOT EXISTS catalog.variant_attributes_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 10;

CREATE TABLE IF NOT EXISTS catalog.variant_attributes
(
    id           BIGINT PRIMARY KEY DEFAULT nextval('catalog.variant_attributes_seq'),
    variant_id   BIGINT,
    attribute_id BIGINT,
    option_id    BIGINT,
    FOREIGN KEY (variant_id) REFERENCES catalog.product_variants (id) ON DELETE CASCADE,
    FOREIGN KEY (attribute_id) REFERENCES catalog.attributes (id),
    FOREIGN KEY (option_id) REFERENCES catalog.attribute_options (id)

)