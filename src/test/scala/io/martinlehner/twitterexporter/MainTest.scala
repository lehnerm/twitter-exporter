package io.martinlehner.twitterexporter

import java.sql.Timestamp
import java.util.Date

import com.danielasfregola.twitter4s.TwitterRestClient
import com.danielasfregola.twitter4s.entities._
import io.martinlehner.twitterexporter.cli.Config
import io.martinlehner.twitterexporter.twitter._
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{AsyncWordSpec, Matchers}
import slick.jdbc.PostgresProfile
import org.mockito.BDDMockito._

import scala.concurrent.Future

class MainTest extends AsyncWordSpec with Matchers with MockitoSugar{

  "flow" should {
    "should pull tweets for the user from twitter and create the user and tweets in the DB" in {
      val tweetRepository = mock[TweetRepository[PostgresProfile]]
      val userRepository = mock[UserRepository[PostgresProfile]]
      val twitterClient = mock[TwitterRestClient]

      given(twitterClient.userTimelineForUser("screen1", count = 3)).willReturn(Future.successful(
        RatedData(RateLimit(1, 2, 3), originalTweets)
      ))

      given(userRepository.create(expectedUser)).willReturn(Future.successful(1))
      given(tweetRepository.createOrUpdateAll(Seq(expectedTweet))).willReturn(Future.successful(1))

      val result = Main.flow(config, twitterClient, tweetRepository, userRepository)

      result.map(code => code shouldEqual 0)
    }

    "fail if no user is configured" in {
      val tweetRepository = mock[TweetRepository[PostgresProfile]]
      val userRepository = mock[UserRepository[PostgresProfile]]
      val twitterClient = mock[TwitterRestClient]

      val result = Main.flow(Config("", 3), twitterClient, tweetRepository, userRepository)

      result.map(code => code shouldEqual 400)
    }

    "fail if user insertion fails" in {
      val tweetRepository = mock[TweetRepository[PostgresProfile]]
      val userRepository = mock[UserRepository[PostgresProfile]]
      val twitterClient = mock[TwitterRestClient]

      given(twitterClient.userTimelineForUser("screen1", count = 3)).willReturn(Future.successful(
        RatedData(RateLimit(1, 2, 3), originalTweets)
      ))

      given(userRepository.create(expectedUser)).willReturn(Future.failed(new RuntimeException("BAEM")))

      val result = Main.flow(config, twitterClient, tweetRepository, userRepository)
      result.map(code => code shouldEqual 500)
    }
  }

  val user = User(
    id = 123, screen_name = "screen1", created_at = new Date(1234567890),
    email = Some("email"), name = "uname", followers_count = 123,
    favourites_count = 1, friends_count = 2, id_str = "123", lang = "en",
    listed_count = 1, profile_background_color = "red", profile_background_image_url = "x",
    profile_image_url = ProfileImage("f", "f", "f", "f"), profile_background_image_url_https = "s",
    profile_text_color = "red", profile_image_url_https = ProfileImage("f", "f", "f", "f"),
    profile_link_color = "red", profile_location = None, profile_sidebar_border_color = "green",
    profile_sidebar_fill_color = "white", statuses_count = 3,
  )

  val expectedUser = DBUser(123, "screen1", new Timestamp(1234567890), Some("email"), "uname", followersCount = 123)
  val expectedTweet = DBTweet(1, "screen1", "Some tweet", 13, new Timestamp(1234567890), Some(123), Seq(DBHashTag(1, "tagme")))
  val config = Config("screen1", 3)

  val originalTweets = Seq(
    Tweet(
      id = 1, id_str = "1", user = Some(user),
      created_at = new Date(1234567890), text = "Some tweet", source = "whatever",
      retweet_count = 13,
      entities = Some(Entities(hashtags = Seq(HashTag("tagme", Seq()))))
    )
  )
}
