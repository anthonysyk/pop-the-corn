package models.themoviedb

import play.api.libs.json.Json

case class Genre(
                id: Int,
                name: String
                )

object Genre {
  implicit val genreFormats = Json.format[Genre]
}