-- Deploy twitterexporter:create_users_table to pg

BEGIN;

CREATE TABLE twitter_users (
  id BIGINT PRIMARY KEY,
  screen_name TEXT,
  created_at TIMESTAMP,
  email TEXT,
  name TEXT,
  followers_count INT
);

COMMIT;
