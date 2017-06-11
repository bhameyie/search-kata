package com.bhameyie.suggester.api.domain

import akka.actor.{ActorRef, ActorSystem}
import com.bhameyie.suggester.api.actors.SupervisionStrategizer
import com.typesafe.config.Config
import org.mongodb.scala.MongoDatabase

object SupervisedActorFactory {
  def createSearchController(config: Config, mongoDatabase: MongoDatabase)
                            (implicit actorSystem: ActorSystem): ActorRef = {
    val cityFinder = createCityFinder(mongoDatabase)
    val ranker = createMatchRanker()

    SupervisionStrategizer.basicSupervision(SearchController(ranker, cityFinder), "cityFinder")

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
