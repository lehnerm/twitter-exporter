package io.martinlehner.twitterexporter.twitter

import java.sql.Timestamp

import slick.basic.DatabaseConfig
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

case class DBTweet(id: Long, handle: String, text: String, retweetCount: Long, createdAt: Timestamp)

class Tweets(tag: Tag) extends Table[DBTweet](tag, "tweets") {
  def id = column[Long]("id", O.PrimaryKey)
  def handle = column[String]("handle")
  def text = column[String]("text")
  def retweetCount = column[Long]("retweet_count")
  def createdAt = column[Timestamp]("created_at")
  def * = (id, handle, text, retweetCount, createdAt) <> (DBTweet.tupled, DBTweet.unapply)
}

class TweetRepository[T <: slick.basic.BasicProfile](dbConfig: DatabaseConfig[T]) extends TableQuery(new Tweets(_)) {
  private[this] val db = dbConfig.db

  def getAll: Future[Seq[DBTweet]] = {
    db.run(this.result)
  }

  def deleteAll: Future[Int] = {
    db.run(this.delete)
  }

  def createOrUpdateAll(tweets: Seq[DBTweet]): Future[Int] = {
    val commands = tweets.map(this.insertOrUpdate)
    val result = db.run(DBIO.sequence(commands))
    result.map(rows => rows.length)
  }
}