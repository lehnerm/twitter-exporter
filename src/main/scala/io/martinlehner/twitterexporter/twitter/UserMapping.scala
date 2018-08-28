package io.martinlehner.twitterexporter.twitter

import java.sql.Timestamp

import com.danielasfregola.twitter4s.entities.Tweet

object UserMapping {
  def from(originalTweet: Tweet): Option[DBUser] = {
    for {
      user <- originalTweet.user
    } yield DBUser(
      user.id,
      user.screen_name,
      new Timestamp(user.created_at.getTime),
      user.email,
      user.name,
      user.followers_count
    )
  }
}
