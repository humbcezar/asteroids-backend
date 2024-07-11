package nasa

import akka.NotUsed
import akka.http.scaladsl.Http
import akka.http.scaladsl.common.{EntityStreamingSupport, JsonEntityStreamingSupport}
import akka.http.scaladsl.model.Uri.Query
import akka.http.scaladsl.model.{HttpRequest, Uri}
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.scaladsl.Source
import com.typesafe.config.{Config, ConfigFactory}
import format.DateFormat.formatter
import nasa.data.{ListRoot, NearEarthObject}
import nasa.data.ListRoot._
import main.Main.system

import java.util.Date

trait Client {
  def getAsteroid(id: String): Source[NearEarthObject, NotUsed]

  def getList(startDate: Option[Date], endDate: Option[Date]): Source[ListRoot, NotUsed]
}

object Client extends Client {
  lazy val config: Config = ConfigFactory.load()
  implicit val jsonStreamingSupport: JsonEntityStreamingSupport = EntityStreamingSupport.json()
  val listUri: Uri = Uri(config.getString("nasa.baseUri") + config.getString("nasa.listPath"))
  val detailsUri: String => Uri = (id: String) => Uri(s"${config.getString("nasa.baseUri")}${config.getString("nasa.detailsPath")}/$id")

  def getList(startDate: Option[Date], endDate: Option[Date]): Source[ListRoot, NotUsed] = {
    val query = (startDate, endDate) match {
      case (Some(startDate), Some(endDate)) => Query(
        "start_date" -> formatter.format(startDate),
        "end_date" -> formatter.format(endDate),
        "detailed" -> "false",
        "api_key" -> config.getString("nasa.apiKey")
      )
      case _ => Query(
        "detailed" -> "false",
        "api_key" -> config.getString("nasa.apiKey")
      )
    }
    Source.future(Http().singleRequest(HttpRequest(
      uri = listUri.withQuery(query)
    ))).flatMapConcat(response => Source.future(Unmarshal(response).to[ListRoot]))
  }

  override def getAsteroid(id: String): Source[NearEarthObject, NotUsed] = {
    Source.future(Http().singleRequest(HttpRequest(
      uri = detailsUri(id).withQuery(Query("api_key" -> config.getString("nasa.apiKey")))
    ))).flatMapConcat(response => Source.future(Unmarshal(response).to[NearEarthObject]))
  }
}
