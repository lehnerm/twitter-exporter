package io.martinlehner.twitterexporter.cli

import org.scalatest.{Matchers, WordSpec}

class ArgumentParserTest extends WordSpec with Matchers {

  "parsing arguments" should {
    "return a valid configuration" in {
      val args = Array(
        "--user", "HANDLE",
        "-n", "22",
      )

      val config = ArgumentParser.parse(args)

      config shouldEqual Some(Config(
        "HANDLE",
        22,
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
