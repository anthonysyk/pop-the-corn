package models.kaggle

import play.api.libs.json.Json

case class Casting(
                    role: String,
                    name: String,
                    facebookLikes: Option[Int]
                  )

object Casting {
  implicit val formatCasting = Json.format[Casting]

  object Role {
    val Director = "Director"
    val ActorPrincipal = "ActorPrincipal"
    val ActorSecondary = "ActorSecondary"
    val ActorOther = "ActorOther"
  }
}
