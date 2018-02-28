package services

import models.{MovieDetails, UserProfile}
import ptc.libraries.WebClient
import io.circe._
import io.circe.generic.auto._
import io.circe.parser._
import io.circe.syntax._

object GraphQLService {

  def getMovieSimilarity(id: Int): Option[String] = {

    val content = s"""{"query":"query {getMovieSimilaritiesById(id: $id){idIndex idMovie similarity { i j value }}}"}"""

    WebClient.doPost("http://localhost:4242/graphql",  content = content,  headers = Map("Content-Type" -> "application/json", "Accept-Encoding" -> "gzip"))
      .toOption
  }

  def getMoviesBasedOnTaste(userProfile: UserProfile): Seq[MovieDetails] = {

    val response = WebClient.doPost("http://localhost:4242/profile",
      content = userProfile.asJson.noSpaces,
      headers = Map("Content-Type" -> "application/json", "Accept-Encoding" -> "gzip"),
      timeout = 100000
    )
      .toOption

    response.flatMap(res => parse(res).right.toOption.getOrElse(Json.Null).as[Seq[MovieDetails]].right.toOption).getOrElse(Nil)

  }

  def main(args: Array[String]): Unit = {
    println(getMovieSimilarity(335676))
  }

}


