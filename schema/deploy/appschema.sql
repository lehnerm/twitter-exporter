-- Deploy twitterexporter:appschema to pg

BEGIN;

CREATE TABLE TWEETS (
  id BIGINT PRIMARY KEY,
  handle VARCHAR(128),
  text TEXT,
  retweet_count INT
);

COMMIT;
