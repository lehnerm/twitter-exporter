package io.martinlehner.twitterexporter.twitter

import java.sql.Timestamp

import org.scalatest.mockito.MockitoSugar
import org.scalatest.{AsyncWordSpec, Matchers}
import slick.basic.DatabaseConfig
import slick.jdbc.PostgresProfile

class UserRepositoryTest extends AsyncWordSpec with Matchers with MockitoSugar {

  "create" should {
    "should create a twitter user in the database" in {
      val user = DBUser(1, "somename", new Timestamp(1234567890), Some("e@mail.com"), "User name", 32131)

      val repo = new UserRepository(
        DatabaseConfig.forConfig[PostgresProfile]("postgres_test")
      )

      for {
        _ <- repo.deleteAll
        _ <- repo.create(user)
        _ <- repo.create(user)
        allUsers <- repo.getAll
      } yield {
        allUsers shouldEqual Seq(user)
      }
    }
  }
}
