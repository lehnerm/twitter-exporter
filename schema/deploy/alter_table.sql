-- Deploy twitterexporter:alter_table to pg

BEGIN;

ALTER TABLE tweets ADD COLUMN created_at TIMESTAMP;

COMMIT;
