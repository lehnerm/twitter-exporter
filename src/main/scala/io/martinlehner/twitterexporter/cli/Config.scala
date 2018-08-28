package io.martinlehner.twitterexporter.cli

case class Config(
  twitterUser: String = "",
  numTweets: Int = 5,
)
