package io.martinlehner.twitterexporter.db

import slick.basic.{BasicProfile, DatabaseConfig}
import slick.jdbc.{H2Profile, PostgresProfile}

trait DBProvider[T <: BasicProfile] {
  def dbConfig: DatabaseConfig[T]
}

trait PostgresDBProvider extends DBProvider[PostgresProfile] {
  override val dbConfig = DatabaseConfig.forConfig("postgres")
}

trait H2DBProvider extends DBProvider[H2Profile] {
  override val dbConfig = DatabaseConfig.forConfig(
    "h2mem"
  )
}
