package models.themoviedb

import play.api.libs.json.Json

case class BelongsToCollection(
                              id: Int,
                              name: String,
                              poster_path: String,
                              backdrop_path: String
                              )

object BelongsToCollection {
  implicit val belongsToCollectionFormats = Json.format[BelongsToCollection]
}