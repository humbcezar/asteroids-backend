package service

import akka.actor.ActorSystem
import akka.stream.scaladsl.Source
import data.{Asteroid, Asteroids}
import nasa.Client
import nasa.data.{DateGroupedNearEarthObjects, EstimatedDiameter, Kilometers, Links, ListRoot, NearEarthObject}
import org.mockito.ArgumentMatchers.any
import org.mockito.MockitoSugar
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike

import java.util.Date
import scala.concurrent.Await
import scala.concurrent.duration.DurationInt
import scala.language.postfixOps

class AsteroidServiceImplementationSpec extends AnyWordSpecLike with Matchers with MockitoSugar {
  implicit def system: ActorSystem = ActorSystem()

  "AsteroidServiceImplementation" should {

    val mockClient = mock[Client]
    val service = new AsteroidServiceImplementation(mockClient)

    "fetch asteroid details successfully" in {
      val nearEarthObject = NearEarthObject("123", "cool", EstimatedDiameter(Kilometers(2.0, 3.0)), List(), false)
      when(mockClient.getAsteroid(any())).thenReturn(Source.single(nearEarthObject))

      val expectedAsteroid = Asteroid("123", "cool", 2.0, 3.0, false, List(), Some(Links("http://localhost:8080/api/asteroid/123", None, None)))

      val result = Await.result(service.getAsteroidDetails("testId").runFold(Seq.empty[Asteroid])(_ :+ _), 2 seconds)

      result shouldBe Seq(expectedAsteroid)
    }

    "fetch a list of asteroids successfully" in {
      val startDate = Some(new Date())
      val endDate = Some(new Date())
      val sortByName = Some(true)
      val nearEarthObject = NearEarthObject("123", "cool", EstimatedDiameter(Kilometers(2.0, 3.0)), List(), false)
      val listRoot = ListRoot(Links("http://nasa?start_date=2021-02-02&end_date=2021-02-03"), 2, DateGroupedNearEarthObjects(Map("2021-02-01" -> List(nearEarthObject))))

      val expectedAsteroids = List(
        Asteroids(
            List(Asteroid("123", "cool", 2.0, 3.0, false, List(), Some(Links("http://localhost:8080/api/asteroid/123", None, None)))),
            Some(Links("http://localhost:8080/api/asteroids?startDate=2021-02-02&endDate=2021-02-03&sortByName=true", None, None))
        )
      )

      when(mockClient.getList(startDate, endDate)).thenReturn(Source.single(listRoot))

      val result = Await.result(service.getAsteroidList(startDate, endDate, sortByName).runFold(Seq.empty[Asteroids])(_ :+ _), 2 seconds)
      result shouldBe expectedAsteroids
    }
  }
}