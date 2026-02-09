--liquibase formatted sql
--changeset JehadHamid:002-create-categories-audit

CREATE TABLE IF NOT EXISTS audit.categories_audit
(
    rev               INT NOT NULL,
    revtype           SMALLINT,
    id                BIGINT,
    name              VARCHAR(50),
    slug              VARCHAR(50),
    targeted_audience VARCHAR(20),
    age_group         VARCHAR(20),
    is_active         BOOLEAN,
    image_file_key    UUID,
    parent_id         BIGINT,
    created_at        timestamp,
    created_by        VARCHAR(50),
    updated_at        timestamp,
    updated_by        VARCHAR(50),
    PRIMARY KEY (id, rev),
    CONSTRAINT fk_categories_audit_rev
        FOREIGN KEY (rev)
            REFERENCES audit.revinfo (rev)
)
