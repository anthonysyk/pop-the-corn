package ptc.libraries

trait CirceHelper {

  import io.circe._
  import io.circe.generic.auto._
  import io.circe.parser._
  import io.circe.syntax._

  case class SearchResponseResponse[T](
                                        hits: Option[Int],
                                        results: Seq[T]
                                      )

  def parseSearchResponseWithHits[T](searchResponse: String)(implicit decoder: Decoder[T]): SearchResponseResponse[T] = {

    val json = parse(searchResponse.toString).right.toOption.getOrElse(Json.Null)

    val results: Seq[T] = json.hcursor.downField("hits").downField("hits").as[Seq[JsonObject]].right.toOption.getOrElse(Seq.empty)
      .map(_.toMap("_source")).flatMap(_.as[T].right.toOption)

    val count: Option[Int] = json.hcursor.downField("hits").downField("total").as[Int].right.toOption

    SearchResponseResponse(count, results)
  }

  def parseProductToMap[A <: Product](product: A)(implicit encoder: Encoder[A]): Map[String, Any] = {
    removeOptionFromMap(parseJsonToMap(product.asJson).asInstanceOf[Map[String, Any]])
  }

  // collect = map + filter
  def removeOptionFromMap(mapping: Map[String, Any]): Map[String, Any] = mapping.collect {
    case (key, Some(result)) => key -> result
    case (key, value: Map[_, _]) => key -> removeOptionFromMap(value.map(value => value._1.toString -> value._2))
    case (key, vector: Vector[_])=> key -> vector.map {
      case mapObject: Map[_, _] => removeOptionFromMap(mapObject.map(value => value._1.toString -> value._2))
      case Some(a: Any) => a
    }
  }


  // Un peu moche parceque pas de tailrec
  def parseJsonToMap(json: Json): Any = json match {
    case value if value.isNull => None
    case value if value.isBoolean => value.asBoolean
    case value if value.isObject => value.asObject.getOrElse(JsonObject.empty).toMap.map { case (key, jsonNested) => key -> parseJsonToMap(jsonNested) }
    case value if value.isArray => value.asArray.getOrElse(Nil).map(parseJsonToMap)
    case value if value.isNumber => value.asNumber
    case value if value.isString => value.asString
  }


}
