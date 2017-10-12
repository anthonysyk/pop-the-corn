package models

import scala.collection.immutable.ListMap

case class UserProfile(
                      genres: ListMap[Int, Double],
                      companies: Map[Int, Double] = Map.empty[Int, Double]
                      )

