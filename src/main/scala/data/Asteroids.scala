package data

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import com.typesafe.config.{Config, ConfigFactory}
import nasa.data.{Links, ListRoot}
import spray.json.{DefaultJsonProtocol, RootJsonFormat}
import nasa.data.ListRoot.linksFormat

case class Asteroids(
  asteroids: List[Asteroid],
  links: Option[Links] = None,
)

object Asteroids extends SprayJsonSupport with DefaultJsonProtocol {
  lazy val config: Config = ConfigFactory.load()

  implicit val format: RootJsonFormat[Asteroids] = jsonFormat2(Asteroids.apply)
  def listFromNasa(listRoot: ListRoot, sortByName: Option[Boolean]): Asteroids = Asteroids(
    asteroids = listRoot.near_earth_objects.near_earth_objects
      .values
      .flatMap(nearEarthObjects => nearEarthObjects.map(Asteroid.fromNearEarthObject))
      .toList
      .sortByName(sortByName),
    links = Some(buildLink(listRoot.links, sortByName))
  )

  private def buildLink(nasaLinks: Links, sortByName: Option[Boolean]): Links = {
    Links(
      s"$buildLink?startDate=${parseNasaStartDate(nasaLinks.self)}&endDate=${parseNasaEndDate(nasaLinks.self)}&sortByName=${sortByName.getOrElse(false)}",
      nasaLinks.previous.map(prev => s"$buildLink?startDate=${parseNasaStartDate(prev)}&endDate=${parseNasaEndDate(prev)}&sortByName=${sortByName.getOrElse(false)}"),
      nasaLinks.next.map(next => s"$buildLink?startDate=${parseNasaStartDate(next)}&endDate=${parseNasaEndDate(next)}&sortByName=${sortByName.getOrElse(false)}"),
    )
  }

  private def parseNasaStartDate(nasaLink: String) = {
    val linkSplitted = nasaLink.split("[?=&]")
    val keyIndex = linkSplitted.indexOf("start_date")
    linkSplitted(keyIndex + 1)
  }

  private def parseNasaEndDate(nasaLink: String) = {
    val linkSplitted = nasaLink.split("[?=&]")
    val keyIndex = linkSplitted.indexOf("end_date")
    linkSplitted(keyIndex + 1)
  }

  private def buildLink = {
    s"http://${config.getString("webserver.hostname")}:${config.getString("webserver.port")}/api/asteroids"
  }
}
