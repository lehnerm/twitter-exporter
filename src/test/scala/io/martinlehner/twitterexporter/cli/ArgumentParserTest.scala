package io.martinlehner.twitterexporter.cli

import org.scalatest.{Matchers, WordSpec}

class ArgumentParserTest extends WordSpec with Matchers {

  "parsing arguments" should {
    "return a valid configuration" in {
      val args = Array(
        "-h", "HANDLE",
        "--twitter-consumer-key", "TW_CK",
        "--twitter-consumer-secret", "TW_CS",
        "--twitter-access-token", "TW_AT",
        "--twitter-access-token-secret", "TW_ATS",
        "--database-url", "DB_URL",
        "--database-username", "DB_USERNAME",
        "--database-password", "DB_PASSWORD",
      )

      val config = ArgumentParser.parse(args)

      config shouldEqual Some(Config(
        "HANDLE",
        DBConfig(
          "DB_URL",
          "DB_USERNAME",
          "DB_PASSWORD",
        ),
        TwitterCredentials(
          "TW_CK",
          "TW_CS",
          "TW_AT",
          "TW_ATS",
        ),
      ))
    }

    "return no configuration if the arguments are invalid" in {
      val args = Array(
        "-h",
      )

      val config = ArgumentParser.parse(args)

      config shouldEqual None
    }
  }
}
