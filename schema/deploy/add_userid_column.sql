-- Deploy twitterexporter:add_userid_column to pg

BEGIN;

ALTER TABLE tweets ADD COLUMN user_id BIGINT;

COMMIT;
