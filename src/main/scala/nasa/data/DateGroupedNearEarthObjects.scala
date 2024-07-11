package nasa.data

case class DateGroupedNearEarthObjects(
  near_earth_objects: Map[String, List[NearEarthObject]]
)