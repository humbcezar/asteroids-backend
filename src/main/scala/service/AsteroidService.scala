package service

import akka.NotUsed
import akka.stream.scaladsl.Source
import data.{Asteroid, Asteroids}
import nasa.Client

import java.util.Date

trait AsteroidService {
  def getAsteroidDetails(id: String): Source[Asteroid, NotUsed]

  def getAsteroidList(startDate: Option[Date], endDate: Option[Date], sortByName: Option[Boolean]): Source[Asteroids, NotUsed]
}

class AsteroidServiceImplementation(httpClient: Client) extends AsteroidService {
  def getAsteroidList(startDate: Option[Date], endDate: Option[Date], sortByName: Option[Boolean]): Source[Asteroids, NotUsed] = {
    httpClient.getList(startDate, endDate).map(Asteroids.listFromNasa(_, sortByName))
  }

  override def getAsteroidDetails(id: String): Source[Asteroid, NotUsed] = httpClient.getAsteroid(id).map(Asteroid.fromNearEarthObject)
}
