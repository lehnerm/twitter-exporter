-- Revert twitterexporter:create_hashtag_table from pg

BEGIN;

DROP TABLE tweet_hashtags;

COMMIT;
