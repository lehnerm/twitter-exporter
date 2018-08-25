package io.martinlehner.twitterexporter

import io.martinlehner.twitterexporter.cli.ArgumentParser

object Main {
  def main(args: Array[String]): Unit = {
    val maybeConfig = ArgumentParser.parse(args)

    println(maybeConfig)
  }
}