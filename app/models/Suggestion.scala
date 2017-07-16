package models

import play.api.libs.json.Json

case class Suggestion (
                      id: Option[Int],
                      title: String,
                      url: Option[String],
                      votes_average: Option[Float],
                      votes: Int
                      )

object Suggestion {
  implicit val formatSuggestion = Json.format[Suggestion]
}