package io.martinlehner.twitterexporter.twitter

import java.sql.Timestamp

import com.danielasfregola.twitter4s.entities.Tweet

object TweetMapping {
  def from(originalTweet: Tweet): DBTweet = {
    DBTweet(
      id = originalTweet.id,
      userId = originalTweet.user.map(_.id),
      handle = originalTweet.user.map(u => u.screen_name).getOrElse("unknown"),
      text = originalTweet.text,
      retweetCount = originalTweet.retweet_count,
      createdAt = new Timestamp(originalTweet.created_at.getTime),
      hashTags = hashTagsForTweet(originalTweet)
    )
  }

  def hashTagsForTweet(tweet: Tweet): Seq[DBHashTag] = {
    for {
      entities <- tweet.entities.toSeq
      tags <- entities.hashtags
      tag <- entities.hashtags
    } yield DBHashTag(tweet.id, tag.text)
  }
}
