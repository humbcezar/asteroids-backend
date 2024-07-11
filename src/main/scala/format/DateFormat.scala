package format

import spray.json.{DefaultJsonProtocol, JsString, JsValue, RootJsonFormat}

import java.text.SimpleDateFormat
import java.util.Date

object DateFormat extends DefaultJsonProtocol {
  val formatter: SimpleDateFormat = {
    val simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd")
    simpleDateFormat.setLenient(false)
    simpleDateFormat
  }

  implicit val dateFormat: RootJsonFormat[Date] = new RootJsonFormat[Date] {
    override def write(date: Date): JsValue = JsString(formatter.format(date))

    override def read(json: JsValue): Date = json match {
      case JsString(s) => try {
        formatter.parse(s)
      } catch {
        case e: Exception => throw new RuntimeException("Invalid date format")
      }
      case _ => throw new RuntimeException("Invalid date format")
    }
  }
}