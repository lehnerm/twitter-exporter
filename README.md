# Tweet Exporter
A simple command-line tool for exporting tweets from Twitter to a SQL database.

## Requirements

### Runtime
- Java 8 JDK installation
- Postgres Server
- Twitter API credentials

### Development
- Scala SBT (https://www.scala-sbt.org/download.html)

## Usage

### Twitter credentials

### Database credentials

### Using the local

## Build
Run `sbt build`

## Test
Run `sbt test`

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