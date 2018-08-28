# Tweet Exporter
A simple command-line tool for exporting tweets from Twitter to a postgres database.

## Requirements

### Runtime
- Java 8 JDK
- Postgres server
- Twitter API credentials

### Development
- Scala SBT (https://www.scala-sbt.org/download.html)
- Docker & docker-compose (https://docker.io)
- sqitch (https://sqitch.org)

## Usage

### Twitter credentials
To access the twitter api you'll need twitter api keys. They can be configured
via the corresponding environment variables:
```
TWITTER_CONSUMER_KEY
TWITTER_CONSUMER_SECRET

TWITTER_ACCESS_TOKEN
TWITTER_ACCESS_TOKEN_SECRET
```

### Database credentials
Either you use the provided docker-compose setup with `docker-compose up`
or you'll have to configure the target db via env variables
```
TWEX_DB_URL: Database jdbc url, for example jdbc:postgresql://localhost:5432/twitter
TWEX_DB_USERNAME: Database user
TWEX_DB_PASSWORD: Database password
```

### Build & Installation
To build the debian package (other OS not supported atm ...), simply run
`sbt debian:packageBin` which will bundle the app up. The resulting package can then
be found in the build directory: `target/twitter-exporter_1.0.0_all.deb`.

Install with `dpkg -i target/twitter-exporter_1.0.0_all.deb` and enjoy the new `twitter-exporter`
command!

### Exporting tweets for a user
Either install via deb package or run directly via sbt with `sbt run --twitter-handle $TWITTER_HANDLE`

## Testing
Bring up the test postgres with `docker-compose up`. At 
the moment, this does not create the appropriate db `twitter_test` nor
initialize the schema with `sqitch`. Once you've done this, you can
run `sbt test` to execute the tests.

## Database schema initialization
All database schema management is done via sqitch. Please, no manual changes!

This assumes you have a target postgres
server and corresponding database already setup.

### Install sqitch
Instructions can be found at https://sqitch.org/

### Configure your sqitch properly

Config postgres psql client location:
`sqitch config --user engine.pg.client /opt/local/pgsql/bin/psql`

Configure your user (for changelog)
`sqitch config --user user.name 'Your Name'`
`sqitch config --user user.email 'email@example.com'`

### Run schema initialization
Run the schema deployment from the schema folder:
`sqitch deploy "db:pg://$USER:$PW@$HOST:$PORT/$DBNAME"`
