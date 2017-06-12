package com.bhameyie.suggester.api.rest

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.testkit.TestKit
import com.bhameyie.suggester.api.rest.dtos.PresentableSuggestion
import com.bhameyie.suggester.database.{ApplicationDatabase, Collections, DatabaseCityRecord, DbCoordinate}
import com.typesafe.config.ConfigFactory
import org.mongodb.scala.Document
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpec}

import scala.concurrent.{Await, ExecutionContextExecutor}

class IntegrationApiSpec extends WordSpec with ScalatestRouteTest with Matchers with BeforeAndAfterAll {

  import io.circe.generic.auto._
  import io.circe.parser.decode

  import scala.concurrent.duration._

  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  private val conf = ConfigFactory.load()
  private val apiRoute = ApiComposer.compose(conf)

  private val db = ApplicationDatabase(conf)

  private val citiesColl = {
    val coll = db.getCollection[DatabaseCityRecord](Collections.cityRecords)
    coll.createIndex(Document("location" -> "2dsphere")).toFuture
    coll
  }

  override def beforeAll(): Unit = {
    Await.result(
      citiesColl.insertMany(
        List(
          DatabaseCityRecord(
            23,
            "New London", "US", DbCoordinate(List(-72.09952, 41.35565)), Some("Connecticut")
          ),
          DatabaseCityRecord(
            23,
            "London", "US", DbCoordinate(List(-84.08326, 37.12898)), Some("Maryland")
          ),
          DatabaseCityRecord(
            23,
            "Quebec", "Canada", DbCoordinate(List(-71.21454, 46.81228)), Some("Quebec")
          )
        )
      ).toFuture(), 10 seconds)
  }

  override def afterAll {
    Await.result(citiesColl.drop().toFuture, 10 seconds)
    TestKit.shutdownActorSystem(system)
  }

  "Health endpoint" should {
    "return 'Healthy'" in {
      Get("/health") ~> apiRoute ~> check {
        status shouldBe StatusCodes.OK
        responseAs[String] shouldBe "\"Healthy\""
      }
    }

  }

  "on valid request, suggestion route" should {
    "return scored set on name only request" in {
      Get("/suggestions?q=Lond") ~> apiRoute ~> check {
        status shouldBe StatusCodes.OK
        val suggestionsEither = decode[List[PresentableSuggestion]](responseAs[String])

        suggestionsEither.isRight shouldBe true

        val suggestions = suggestionsEither.right.get

        suggestions.size shouldBe 2
        suggestions.foreach { s =>
          s.name.contains("Lond") shouldBe true
          s.score should be > 0.5
        }
      }
    }

    "return proximity sorted set on coordinate request" in {
      Get("/suggestions?q=Lond&long=-71.21454&lat=46.81228") ~> apiRoute ~> check {
        status shouldBe StatusCodes.OK
        val suggestionsEither = decode[List[PresentableSuggestion]](responseAs[String])

        suggestionsEither.isRight shouldBe true

        val suggestions = suggestionsEither.right.get

        suggestions.size shouldBe 2
        suggestions.foreach { s =>
          s.name.contains("Lond") shouldBe true
          s.score should be > 0.5
        }

        suggestions.map(_.name) shouldBe List("New London, Connecticut, US", "London, Maryland, US")
      }
    }
  }

  "on Invalid request, suggestion rout" should {
    "return validation error" in {
      Get("/suggestions?q=") ~> apiRoute ~> check {
        status shouldBe StatusCodes.BadRequest
        responseAs[String] shouldBe "\"The search query must have a value\""
      }
    }
  }
}
