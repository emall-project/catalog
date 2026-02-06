--liquibase formatted sql
--changeset JehadHamid:005-create-tags-audit

CREATE TABLE IF NOT EXISTS audit.tags_audit(
        rev INT NOT NULL,
        revtype SMALLINT,
        id BIGINT,
        name VARCHAR(50),
        PRIMARY KEY (id, rev),
        CONSTRAINT fk_tags_audit_rev
        FOREIGN KEY (rev)
            REFERENCES audit.revinfo (rev)
)
