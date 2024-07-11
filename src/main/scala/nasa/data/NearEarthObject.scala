package nasa.data

case class NearEarthObject(
  id: String,
  name: String,
  estimated_diameter: EstimatedDiameter,
  close_approach_data: List[CloseApproachData],
  is_potentially_hazardous_asteroid: Boolean
)