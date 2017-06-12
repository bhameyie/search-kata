package com.bhameyie.suggester.api.domain

import akka.actor.{ActorRef, ActorSystem}
import com.bhameyie.suggester.actors.SupervisionStrategizer
import com.typesafe.config.Config
import org.mongodb.scala.MongoDatabase

/**
  * Produces the actors used in the api
  */
object SupervisedActorFactory {
  def createSearchController(config: Config, mongoDatabase: MongoDatabase)
                            (implicit actorSystem: ActorSystem): ActorRef = {
    val cityFinder = createCityFinder(mongoDatabase)
    val ranker = createMatchRanker()

    SupervisionStrategizer.basicSupervision(SearchController(ranker, cityFinder), "searchController")

  }

  private def createCityFinder(mongoDatabase: MongoDatabase)
                              (implicit actorSystem: ActorSystem): ActorRef = {
    SupervisionStrategizer.basicSupervision(CityFinder(mongoDatabase), "cityFinder")
  }

  private def createMatchRanker()
                               (implicit actorSystem: ActorSystem): ActorRef = {
    SupervisionStrategizer.basicSupervision(MatchRanker(), "matchRanker")
  }
}
