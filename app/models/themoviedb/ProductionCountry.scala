package models.themoviedb

import play.api.libs.json.Json

case class ProductionCountry(
                            iso_3166_1: String,
                            name: String
                            )

object ProductionCountry {
  implicit val productionCountry = Json.format[ProductionCountry]
}