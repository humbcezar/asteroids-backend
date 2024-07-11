package main

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.common.{EntityStreamingSupport, JsonEntityStreamingSupport}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.scaladsl.Flow
import akka.util.ByteString
import com.typesafe.config.{Config, ConfigFactory}
import format.DateFormat.formatter
import nasa.Client
import service.{AsteroidService, AsteroidServiceImplementation}

object Main extends App {

  lazy val config: Config = ConfigFactory.load()
  implicit val jsonStreamingSupport: JsonEntityStreamingSupport =
    EntityStreamingSupport.json(1).withFramingRenderer(Flow[ByteString])
  implicit val system = ActorSystem(Behaviors.empty, "Asteroids")
  implicit val executionContext = system.executionContext
  val httpClient: Client = Client
  val asteroidService: AsteroidService = new AsteroidServiceImplementation(httpClient)

  def createRoute(asteroidService: AsteroidService): Route = pathPrefix("api") {
    path("asteroids") {
      get {
        parameters("startDate".as[String].optional, "endDate".as[String].optional, "sortByName".as[Boolean].optional) { (startDate, endDate, sortByName) =>
          complete(asteroidService.getAsteroidList(startDate.map(formatter.parse), endDate.map(formatter.parse), sortByName))
        }
      }
    } ~
      path("asteroid" / Segment) { id =>
        get {
          complete(asteroidService.getAsteroidDetails(id))
        }
      }
  }

  Http().newServerAt(config.getString("webserver.hostname"), config.getInt("webserver.port")).bind(createRoute(asteroidService))
}
