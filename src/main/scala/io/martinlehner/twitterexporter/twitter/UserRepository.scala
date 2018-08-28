package io.martinlehner.twitterexporter.twitter

import java.sql.Timestamp

import slick.basic.DatabaseConfig
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.Future

case class DBUser(
                    id: Long,
                    screenName: String,
                    createdAt: Timestamp,
                    email: Option[String],
                    name: String,
                    followersCount: Int,
                  )

class Users(tag: Tag) extends Table[DBUser](tag, "twitter_users") {
  def id = column[Long]("id", O.PrimaryKey)
  def screenName = column[String]("screen_name")
  def createdAt = column[Timestamp]("created_at")
  def email = column[Option[String]]("email")
  def name = column[String]("name")
  def followersCount = column[Int]("followers_count")

  type Data = (Long, String, String, Long, Timestamp)

  def * = (id, screenName, createdAt, email, name, followersCount) <> (DBUser.tupled, DBUser.unapply)
}

class UserRepository[T <: slick.basic.BasicProfile](dbConfig: DatabaseConfig[T]) extends TableQuery(new Users(_)) {
  private[this] val db = dbConfig.db

  def getAll: Future[Seq[DBUser]] = {
    db.run(this.result)
  }

  def deleteAll: Future[Int] = {
    db.run(this.delete)
  }

  def create(user: DBUser): Future[Int] = {
    db.run(this.insertOrUpdate(user))
  }
}