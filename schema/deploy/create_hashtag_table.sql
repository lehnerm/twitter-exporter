-- Deploy twitterexporter:create_hashtag_table to pg

BEGIN;

CREATE TABLE tweet_hashtags (
  tweet_id BIGINT,
  tag TEXT
);

COMMIT;
