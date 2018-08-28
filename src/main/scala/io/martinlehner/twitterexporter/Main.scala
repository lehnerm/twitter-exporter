package io.martinlehner.twitterexporter

import akka.actor.ActorSystem
import com.danielasfregola.twitter4s.TwitterRestClient
import com.danielasfregola.twitter4s.exceptions.TwitterException
import com.typesafe.scalalogging.LazyLogging
import io.martinlehner.twitterexporter.twitter.{TweetMapping, TweetRepository, UserMapping, UserRepository}
import io.martinlehner.twitterexporter.cli.{ArgumentParser, Config}
import slick.basic.DatabaseConfig
import slick.jdbc.{JdbcProfile, PostgresProfile}

import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.util.control.NonFatal
import scala.util.{Failure, Success}

object Main extends LazyLogging {
  val userError = 400
  val systemError = 500
  val ok = 0

  def main(args: Array[String]): Unit = {
    val maybeConfig = ArgumentParser.parse(args)
    implicit val system: ActorSystem = ActorSystem("twitter-exporter")

    def exit(code: Int) = {
      Await.ready(system.terminate(), 10.seconds)
      sys.exit(code)
    }

    try {
      val dbConfig = DatabaseConfig.forConfig[PostgresProfile]("postgres")
      val tweetRepository = new TweetRepository[PostgresProfile](
        dbConfig,
      )

      val userRepository = new UserRepository[PostgresProfile](
        dbConfig,
      )

      val twitterClient = TwitterRestClient.withActorSystem(system)

      maybeConfig.foreach(config => {
        val exitCode = Await.result(
          flow(config, twitterClient, tweetRepository, userRepository), 10.seconds
        )

        exit(exitCode)
      })
    } catch {
      case NonFatal(e) =>
        logger.error(s"Application failed to initialize", e)
        exit(systemError)
    }
  }

  def flow[T <: JdbcProfile](config: Config,
                             twitterClient: TwitterRestClient,
                             tweetRepository: TweetRepository[T],
                             userRepository: UserRepository[T]): Future[Int] = {

    if (config.twitterUser.isEmpty) {
      logger.error(s"No twitter user to export specified. Use --user <value> to specify a user.")
      Future.successful(userError)
    } else {
      logger.info(s"Fetching tweets for ${config.twitterUser} ...")
      val tweets_> = twitterClient.userTimelineForUser(config.twitterUser, count = config.numTweets)
        .flatMap(result => {
          val tweets = result.data

          logger.info(s"Fetched ${tweets.length} tweets for ${config.twitterUser}. Storing in Database ...")
          val tweetUsers = for {
            tweet <- tweets
            user <- UserMapping.from(tweet)
          } yield user

          val maybeUser_> = tweetUsers.headOption.map(
            user => userRepository.create(user)
          )

          val dbTweets = tweets.map(TweetMapping.from)

          for {
            _ <- maybeUser_>.getOrElse(Future.successful(0))
            tweetInserResult <- tweetRepository.createOrUpdateAll(dbTweets)
          } yield tweetInserResult
        })

      tweets_>.transform(result => result match {
        case Success(value) =>
          logger.info(s"Successfully inserted $value tweets into the database.")
          Success(ok)

        case Failure(t: TwitterException) =>
          logger.error(s"Twitter error while retrieving tweets: ${t.code} ${t.errors}")
          Success(userError)

        case Failure(t: Throwable) =>
          logger.error(s"Failure while retrieving tweets", t)
          Success(systemError)
      })
    }
  }
}