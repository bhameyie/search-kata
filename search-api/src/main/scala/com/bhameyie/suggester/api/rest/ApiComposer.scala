package com.bhameyie.suggester.rest

import java.util.UUID
import java.util.concurrent.TimeUnit

import akka.actor.ActorSystem
import akka.event.{Logging, LoggingAdapter}
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server._
import akka.pattern.ask
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.StreamConverters
import akka.util.Timeout
import cats.data.Validated.{Invalid, Valid}
import com.typesafe.config.Config

import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration._
import scala.util.{Failure, Success, Try}
import akka.pattern.ask
import com.bhameyie.suggester.api.database.ApplicationDatabase
import com.bhameyie.suggester.api.domain.SupervisedActorFactory
import com.bhameyie.suggester.api.domain.protocols.SearchResult
import com.bhameyie.suggester.api.rest.dtos.{PresentableSuggestions, SearchRequest}

object ApiComposer {

  import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
  import io.circe.generic.auto._

  def compose(conf: Config)(
    implicit actorSystem: ActorSystem,
    actorMaterializer: ActorMaterializer,
    executionContext: ExecutionContext): Route = {

    implicit val timeout: Timeout = 10 seconds
    implicit val rejectionHandler = RejectionHandler.default

    val logger: LoggingAdapter = Logging(actorSystem, "ProfileApiLayer")

    val database = ApplicationDatabase(conf)

    val searchController = SupervisedActorFactory.createSearchController(conf, database)

    path("health") {
      get {
        complete("Healthy")
      }
    } ~ path("suggestions") {
      parameters('q.as[String], 'long.?, 'lat.?) { (q, longitude, latitude) =>

        val pipeline = Validator.validate(SearchRequest(q, longitude, latitude))
          .map(criteria => (searchController ? criteria).mapTo[SearchResult])
          .map(fut => fut.map(d => PresentableSuggestions(d)))

        pipeline match {
          case Valid(a) => onComplete(a) {
            case Success(res) =>
              complete(res)
            case Failure(ex) =>
              logger.error(ex, ex.getMessage)
              complete((StatusCodes.InternalServerError, "An error occurred while performing your operation. Please try again."))
          }

          case Invalid(e) => complete((StatusCodes.BadRequest, e.toString))
        }
      }
    }
  }

}
