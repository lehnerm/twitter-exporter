-- Revert twitterexporter:create_users_table from pg

BEGIN;

DROP TABLE twitter_users;

COMMIT;
