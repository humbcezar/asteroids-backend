package nasa.data

case class CloseApproachData(
  close_approach_date: String,
  relative_velocity: RelativeVelocity,
  orbiting_body: String
)