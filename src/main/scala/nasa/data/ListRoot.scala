package nasa.data

import spray.json.{DefaultJsonProtocol, JsArray, JsObject, JsValue, RootJsonFormat, enrichAny}
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport

case class ListRoot(
  links: Links,
  element_count: Int,
  near_earth_objects: DateGroupedNearEarthObjects
)

object ListRoot extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val linksFormat: RootJsonFormat[Links] = jsonFormat3(Links)
  implicit val kilometersFormat: RootJsonFormat[Kilometers] = jsonFormat2(Kilometers)
  implicit val estimatedDiameterFormat: RootJsonFormat[EstimatedDiameter] = jsonFormat1(EstimatedDiameter)
  implicit val relativeVelocityFormat: RootJsonFormat[RelativeVelocity] = jsonFormat1(RelativeVelocity)
  implicit val closeApproachDataFormat: RootJsonFormat[CloseApproachData] = jsonFormat3(CloseApproachData)
  implicit val nearEarthObjectFormat: RootJsonFormat[NearEarthObject] = jsonFormat5(NearEarthObject.apply)

  implicit object DateGroupedNearEarthObjectsFormat extends RootJsonFormat[DateGroupedNearEarthObjects] {
    def write(obj: DateGroupedNearEarthObjects): JsValue =
      JsObject(obj.near_earth_objects.map { case (date, nearEarthObjects) =>
        date -> JsArray(nearEarthObjects.map(_.toJson).toVector)
      })

    def read(json: JsValue): DateGroupedNearEarthObjects =
      DateGroupedNearEarthObjects(json.asJsObject.fields.map { case (date, nearEarthObjectsJsArray) =>
        date -> nearEarthObjectsJsArray.convertTo[List[NearEarthObject]]
      })
  }

  implicit val rootFormat: RootJsonFormat[ListRoot] = jsonFormat3(ListRoot.apply)
}
