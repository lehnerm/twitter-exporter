package io.martinlehner.twitterexporter.cli

object ArgumentParser {
  private[this] val programName = getClass.getPackage.getImplementationTitle
  private[this] val buildVersion = getClass.getPackage.getImplementationVersion

  private[this] val parser = new scopt.OptionParser[Config](programName) {
    head(
      programName, buildVersion,
    )

    note("Export configuration:")
    opt[String]('h', "twitter-handle").action((x, c) =>
      c.copy(twitterHandle = x)).text("Twitter handle to export the latest tweets from")

    note("\nTwitter credentials:")
    opt[String]("twitter-consumer-key").action((x, c) =>
      c.copy(twitterCredentials = c.twitterCredentials.copy(
        consumerKey = x,
      ))).text("Set Twitter consumer key. Defaults to $TWITTER_CONSUMER_KEY")

    opt[String]("twitter-consumer-secret").action((x, c) =>
      c.copy(twitterCredentials = c.twitterCredentials.copy(
        consumerSecret = x,
      ))).text("Set Twitter consumer secret. Defaults to $TWITTER_CONSUMER_SECRET")

    opt[String]("twitter-access-token").action((x, c) =>
      c.copy(twitterCredentials = c.twitterCredentials.copy(
        accessToken = x,
      ))).text("Set Twitter consumer key. Defaults to $TWITTER_ACCESS_TOKEN")

    opt[String]("twitter-access-token-secret").action((x, c) =>
      c.copy(twitterCredentials = c.twitterCredentials.copy(
        accessTokenSecret = x,
      ))).text("Set Twitter access token secret. Defaults to $TWITTER_ACCESS_TOKEN_SECRET")

    note("\nDatabase configuration:")
    opt[String]("database-url").action((x, c) =>
      c.copy(dbConfig = c.dbConfig.copy(
        url = x,
      ))).text("Set database url. Defaults to $TWEX_DB_URL")

    opt[String]("database-username").action((x, c) =>
      c.copy(dbConfig = c.dbConfig.copy(
        username = x,
      ))).text("Set database user. Defaults to $TWEX_DB_USERNAME")

    opt[String]("database-password").action((x, c) =>
      c.copy(dbConfig = c.dbConfig.copy(
        password = x,
      ))).text("Set database password. Defaults to $TWEX_DB_PASSWORD")


    note("Exports the latest tweets of a twitter handle to a SQL Database.\n")
  }

  def parse(args: Array[String]): Option[Config] = {
    parser.parse(args, Config())
  }
}