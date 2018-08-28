package io.martinlehner.twitterexporter.twitter

import java.sql.Timestamp

import slick.basic.DatabaseConfig
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

case class DBTweet(
                    id: Long,
                    handle: String,
                    text: String,
                    retweetCount: Long,
                    createdAt: Timestamp,
                    userId: Option[Long],
                    hashTags: Seq[DBHashTag] = Seq(),
                  )
case class DBHashTag(tweetId: Long, tag: String)

class Tweets(tag: Tag) extends Table[DBTweet](tag, "tweets") {
  def id = column[Long]("id", O.PrimaryKey)
  def handle = column[String]("handle")
  def userId = column[Option[Long]]("user_id")
  def text = column[String]("text")
  def retweetCount = column[Long]("retweet_count")
  def createdAt = column[Timestamp]("created_at")

  type Data = (Long, String, String, Long, Timestamp, Option[Long])

  def constructTweet: Data => DBTweet = {
    case (id, handle, text, retweetCount, createdAt, userId) =>
      DBTweet(id, handle, text, retweetCount, createdAt, userId)
  }

  def extractTweet: PartialFunction[DBTweet, Data] = {
    case DBTweet(id, handle, text, retweetCount, createdAt, userId, _) =>
      (id, handle, text, retweetCount, createdAt, userId)
  }

  def * = (id, handle, text, retweetCount, createdAt, userId) <> (constructTweet, extractTweet.lift)
}

class HashTags(tag: Tag) extends Table[DBHashTag](tag, "tweet_hashtags") {
  def tweetId = column[Long]("tweet_id")
  def hashTag = column[String]("tag")

  def * = (tweetId, hashTag) <> (DBHashTag.tupled, DBHashTag.unapply)
}

class TweetRepository[T <: slick.basic.BasicProfile](dbConfig: DatabaseConfig[T]) extends TableQuery(new Tweets(_)) {
  private[this] val db = dbConfig.db
  private[this] val hashTagTable = TableQuery(new HashTags(_))

  def getAll: Future[Seq[DBTweet]] = {
    db.run(this.result)
  }

  def deleteAll: Future[Int] = {
    for {
      tweetDelResult <- db.run(this.delete)
      hashtagDelResult <- db.run(hashTagTable.delete)
    } yield tweetDelResult
  }

  def createOrUpdateAll(tweets: Seq[DBTweet]): Future[Int] = {
    val tweetCommands = tweets.map(this.insertOrUpdate)
    val hashTags = tweets.flatMap(_.hashTags)
    val hashTagCommands = hashTagTable ++= hashTags

    val tweetResult_> = db.run(DBIO.sequence(tweetCommands))
    val hashtagResult_> = db.run(hashTagCommands)

    for {
      tweetResult <- tweetResult_>
      hashtagResult_> <- hashtagResult_>
    } yield tweetResult.sum
  }
}