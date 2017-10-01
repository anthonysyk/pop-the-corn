package services

import ptc.libraries.WebClient

object GraphQLService {

  def getMovieSimilarity(id: Int): Option[String] = {

    val content = s"""{"query":"query {getMovieSimilaritiesById(id: $id){idIndex idMovie similarity { i j value }}}"}"""

    WebClient.doPost("http://localhost:4242/graphql",  content = content,  headers = Map("Content-Type" -> "application/json", "Accept-Encoding" -> "gzip"))
      .toOption

  }

  def main(args: Array[String]): Unit = {
    println(getMovieSimilarity(335676))
  }

}


