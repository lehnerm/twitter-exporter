package io.martinlehner.twitterexporter.cli

case class Config(
  twitterHandle: String = "",
  numTweets: Int = 5,
)
