package io.martinlehner.twitterexporter.twitter

import java.sql.Timestamp

import org.scalatest.mockito.MockitoSugar
import org.scalatest.{AsyncWordSpec, Matchers}
import slick.basic.DatabaseConfig
import slick.jdbc.PostgresProfile

class TweetRepositoryTest extends AsyncWordSpec with Matchers with MockitoSugar {

  "createOrUpdateAll" should {
    "should create or update all tweets in the database" in {
      val tweets = Seq(
        DBTweet(1, "user1", "Some text for t1", 3l, new Timestamp(1234567890), None, Seq(DBHashTag(1, "tagme"))),
        DBTweet(2, "user2", "Sample text for t2", 5l, new Timestamp(1234567891), Some(1234), Seq())
      )

      val repo = new TweetRepository(
        DatabaseConfig.forConfig[PostgresProfile]("postgres_test")
      )

      for {
        _ <- repo.deleteAll
        _ <- repo.createOrUpdateAll(tweets)
        _ <- repo.createOrUpdateAll(tweets)
        allTweets <- repo.getAll
      } yield {
        allTweets shouldEqual tweets.map(_.copy(hashTags = Seq()))
      }
    }
  }
}
