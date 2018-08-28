-- Revert twitterexporter:appschema from pg

BEGIN;

DROP TABLE tweets;

COMMIT;
