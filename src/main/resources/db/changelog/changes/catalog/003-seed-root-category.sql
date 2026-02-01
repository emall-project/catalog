-- liquibase formatted sql
-- changeset JehadHamid:003-seed-root-category runOnChange:false

INSERT INTO catalog.categories (name, slug, parent_id, created_by)
VALUES ('root', 'root', null, 1);
