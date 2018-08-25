package io.martinlehner.twitterexporter.cli

import util.Properties.envOrElse

case class TwitterCredentials(
  consumerKey: String = envOrElse("TWITTER_CONSUMER_KEY", ""),
  consumerSecret: String = envOrElse("TWITTER_CONSUMER_SECRET", ""),
  accessToken: String =  envOrElse("TWITTER_ACCESS_TOKEN", ""),
  accessTokenSecret: String = envOrElse("TWITTER_ACCESS_TOKEN_SECRET", ""),
)

case class DBConfig(
  url: String = envOrElse("TWEX_DB_URL", ""),
  username: String = envOrElse("TWEX_DB_USERNAME", ""),
  password: String = envOrElse("TWEX_DB_PASSWORD", ""),
)

case class Config(
  twitterHandle: String = "",
  dbConfig: DBConfig = DBConfig(),
  twitterCredentials: TwitterCredentials = TwitterCredentials()
)
