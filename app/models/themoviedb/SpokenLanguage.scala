package models.themoviedb

import play.api.libs.json.Json

case class SpokenLanguage(
                         iso_639_1: String,
                         name: String
                         )

object SpokenLanguage {
  implicit val spokenLanguageFormats = Json.format[SpokenLanguage]
}