package data

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import com.typesafe.config.{Config, ConfigFactory}
import spray.json._
import format.DateFormat.{dateFormat, formatter}
import nasa.data.{Links, NearEarthObject}
import nasa.data.ListRoot.linksFormat

import java.util.Date

case class Asteroid(
  id: String,
  name: String,
  estimatedDiameterMin: Double,
  estimatedDiameterMax: Double,
  isPotentiallyHazardous: Boolean,
  additionalData: List[AsteroidAdditionalData],
  links: Option[Links] = None
)

case class AsteroidAdditionalData(
  closeApproachDate: Date,
  speed: String,
  orbitingBody: String,
)

object AsteroidAdditionalData extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val format: RootJsonFormat[AsteroidAdditionalData] = jsonFormat3(AsteroidAdditionalData.apply)
}

object Asteroid extends SprayJsonSupport with DefaultJsonProtocol {
  lazy val config: Config = ConfigFactory.load()

  implicit val format: RootJsonFormat[Asteroid] = jsonFormat7(Asteroid.apply)

  def fromNearEarthObject(nearEarthObject: NearEarthObject): Asteroid =
    Asteroid(
      nearEarthObject.id,
      nearEarthObject.name,
      nearEarthObject.estimated_diameter.kilometers.estimated_diameter_min,
      nearEarthObject.estimated_diameter.kilometers.estimated_diameter_max,
      nearEarthObject.is_potentially_hazardous_asteroid,
      nearEarthObject.close_approach_data.map(data => AsteroidAdditionalData(formatter.parse(data.close_approach_date), data.relative_velocity.kilometers_per_hour, data.orbiting_body)),
      Some(Links(self = buildSelfLink(nearEarthObject.id)))
    )

  private def buildSelfLink(id: String): String = {
    s"http://${config.getString("webserver.hostname")}:${config.getString("webserver.port")}/api/asteroid/$id"
  }

  implicit class AsteroidSortByName(list: List[Asteroid]) {
    def sortByName(enabled: Option[Boolean]): List[Asteroid] = enabled match {
      case Some(true) => list.sortBy(_.name)
      case _ => list
    }
  }
}
