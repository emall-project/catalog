--liquibase formatted sql
--changeset JehadHamid:002-create-brands-audit

CREATE TABLE IF NOT EXISTS audit.brands_audit(
         rev INT NOT NULL,
         revtype SMALLINT,
         id BIGINT,
         name VARCHAR(50),
         slug VARCHAR(50),
         is_active BOOLEAN,
         image_file_key UUID,
         created_at timestamp,
         created_by VARCHAR(50),
         updated_at  timestamp,
         updated_by VARCHAR(50),
         PRIMARY KEY (id, rev),
         CONSTRAINT fk_brands_audit_rev
             FOREIGN KEY (rev)
                 REFERENCES audit.revinfo (rev)
)
