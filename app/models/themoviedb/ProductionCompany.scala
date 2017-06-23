package models.themoviedb

import play.api.libs.json.Json

case class ProductionCompany(
                              name: String,
                              id: Int
                            )

object ProductionCompany {
  implicit val productionCompany = Json.format[ProductionCompany]
}