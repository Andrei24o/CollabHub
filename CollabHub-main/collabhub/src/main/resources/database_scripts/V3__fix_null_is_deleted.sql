-- V3__fix_null_is_deleted.sql

UPDATE projects SET is_deleted = false WHERE is_deleted IS NULL;
ALTER TABLE projects ALTER COLUMN is_deleted SET DEFAULT false;
ALTER TABLE projects ALTER COLUMN is_deleted SET NOT NULL;
