package com.bhameyie.suggester.api.domain.actors

import akka.actor.{ActorRef, ActorSystem}
import com.typesafe.config.Config
import org.mongodb.scala.MongoDatabase

/**
  * Created by bhameyie on 6/10/17.
  */
object SupervisedActorFactory {
  def createSearchController(config: Config, mongoDatabase: MongoDatabase)
                            (implicit actorSystem: ActorSystem): ActorRef = {
    ???
  }

  private def createCityFinder(config: Config, mongoDatabase: MongoDatabase)
                              (implicit actorSystem: ActorSystem): ActorRef = {
    ???
  }

  private def createResultRanker(config: Config, mongoDatabase: MongoDatabase)
                              (implicit actorSystem: ActorSystem): ActorRef = {
    ???
  }
}
