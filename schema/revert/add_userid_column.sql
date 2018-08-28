-- Revert twitterexporter:add_userid_column from pg

BEGIN;

ALTER TABLE tweets DROP COLUMN user_id;

COMMIT;
