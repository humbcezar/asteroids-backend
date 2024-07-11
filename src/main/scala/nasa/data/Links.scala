package nasa.data

case class Links(
  self: String,
  previous: Option[String] = None,
  next: Option[String] = None,
)