package graphql


import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server._
import akka.stream.ActorMaterializer
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import contentbased.ContentBasedRecommendation
import models.UserProfile
import sangria.parser.QueryParser
import sangria.execution.{ErrorWithResolver, Executor, QueryAnalysisError}
import sangria.marshalling.sprayJson._
import spray.json._
import scala.util.{Failure, Success}

object GraphQLServer {

  def main(args: Array[String]): Unit = {

    import io.circe._
    import io.circe.generic.auto._
    import io.circe.parser._
    import io.circe.syntax._

    val repo = GraphQLState.createState()

    ContentBasedRecommendation.computeMoviesState()

    implicit val system = ActorSystem("sangria-server")
    implicit val materializer = ActorMaterializer()

    import system.dispatcher

    val route: Route =
      (post & path("graphql")) {
        entity(as[JsValue]) { requestJson ⇒
          val JsObject(fields) = requestJson

          val JsString(query) = fields("query")

          val operation = fields.get("operationName") collect {
            case JsString(op) ⇒ op
          }

          val vars = fields.get("variables") match {
            case Some(obj: JsObject) ⇒ obj
            case _ ⇒ JsObject.empty
          }

          QueryParser.parse(query) match {

            // query parsed successfully, time to execute it!
            case Success(queryAst) ⇒ {
              complete(Executor.execute(SchemaDefinition.schema, queryAst, repo,
                variables = vars,
                operationName = operation
              ).map(OK → _)
                .recover {
                  case error: QueryAnalysisError ⇒ BadRequest → error.resolveError
                  case error: ErrorWithResolver ⇒ InternalServerError → error.resolveError
                })
            }

            // can't parse GraphQL query, return error
            case Failure(error)
            ⇒
              complete(BadRequest, JsObject("error" → JsString(error.getMessage)))
          }
        }
      } ~ get {
        getFromFile(s"src/main/resources/graphql.html")
      } ~
        path("profile") {
          post {
            entity(as[String]) { json =>

              val userProfile = decode[UserProfile](json).right.toOption
              complete(userProfile.map(ContentBasedRecommendation.computeUserProfileRecommendation).getOrElse(Nil).asJson.noSpaces)
            }
          }
        }

    Http().bindAndHandle(route, "0.0.0.0", sys.props.get("http.port").fold(4242)(_.toInt))

    println("GraphQL Server Started")
  }
}
