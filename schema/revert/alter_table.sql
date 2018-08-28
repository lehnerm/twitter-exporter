-- Revert twitterexporter:alter_table from pg

BEGIN;

ALTER TABLE tweets DROP COLUMN created_at;

COMMIT;
