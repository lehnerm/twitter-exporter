package io.martinlehner.twitterexporter.twitter

import java.sql.Timestamp

import com.danielasfregola.twitter4s.entities.Tweet

object TweetExtracter {
  def from(originalTweet: Tweet): DBTweet = {
    DBTweet(
      id = originalTweet.id,
      handle = originalTweet.user.map(u => u.screen_name).getOrElse("unknown"),
      text = originalTweet.text,
      retweetCount = originalTweet.retweet_count,
      createdAt = new Timestamp(originalTweet.created_at.getTime),
    )
  }
}
