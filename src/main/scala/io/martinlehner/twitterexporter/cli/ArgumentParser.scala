package io.martinlehner.twitterexporter.cli

object ArgumentParser {
  private[this] val programName = getClass.getPackage.getImplementationTitle
  private[this] val buildVersion = getClass.getPackage.getImplementationVersion

  private[this] val parser = new scopt.OptionParser[Config](programName) {
    head(
      programName, buildVersion,
    )

    note("Export configuration:")
    opt[String]('u', "user").action((value, config) =>
      config.copy(twitterUser = value)).text("Twitter user to export the latest tweets from")
    opt[Int]('n', "num-tweets").action((value, config) =>
      config.copy(numTweets = value)).text("Retrieve up this amount of the users latest tweets")

    note("Exports the latest tweets of a twitter handle to a SQL Database.\n")
  }

  def parse(args: Array[String]): Option[Config] = {
    parser.parse(args, Config())
  }
}