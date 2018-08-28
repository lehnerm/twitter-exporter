package io.martinlehner.twitterexporter

import akka.actor.ActorSystem
import com.danielasfregola.twitter4s.TwitterRestClient
import com.danielasfregola.twitter4s.exceptions.TwitterException
import com.typesafe.scalalogging.LazyLogging
import io.martinlehner.twitterexporter.twitter.{TweetExtracter, TweetRepository}
import io.martinlehner.twitterexporter.cli.ArgumentParser
import slick.basic.DatabaseConfig
import slick.jdbc.PostgresProfile

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.util.{Failure, Success}

object Main extends LazyLogging {

  def main(args: Array[String]): Unit = {
    val maybeConfig = ArgumentParser.parse(args)
    implicit val system: ActorSystem = ActorSystem("twitter-exporter")

    try {
      val tweetRepository = new TweetRepository[PostgresProfile](
        DatabaseConfig.forConfig("postgres")
      )

      maybeConfig.foreach(config => {
        val twitterClient = TwitterRestClient.withActorSystem(system)

        logger.info(s"Fetching tweets for ${config.twitterHandle} ...")
        val tweets_> = twitterClient.userTimelineForUser(config.twitterHandle, count = config.numTweets)
          .flatMap(result => {
            val tweets = result.data

            logger.info(s"Fetched ${tweets.length} tweets for ${config.twitterHandle}. Storing in Database ...")
            val dbTweets = tweets.map(TweetExtracter.from)
            tweetRepository.createOrUpdateAll(dbTweets)
          })

        val exitCode_> = tweets_>.transform(result => result match {
          case Success(value) =>
            logger.info(s"Successfully inserted $value tweets into the database.")
            Success(0)

          case Failure(t: TwitterException) =>
            logger.error(s"Twitter error while retrieving tweets: ${t.code} ${t.errors}")
            Success(400)

          case Failure(t: Throwable) =>
            logger.error(s"General failure while retrieving tweets", t)
            Success(500)
        })

        val exitCode = Await.result(exitCode_>, 10.seconds)
        sys.exit(exitCode)
      })
    } finally {
      Await.ready(system.terminate(), 10.seconds)
    }
  }
}