package models

case class GenreProfile(ids: Seq[Int], preference: Double)

case class UserProfile(
                      genres: Seq[GenreProfile],
                      companies: Seq[(Int, Double)] = Nil
                      )

case class DisplayProfile(
                         movieId: String,
                         genres: Map[String, Double],
                         companies: Map[String, Double]
                         )