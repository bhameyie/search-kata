package com.bhameyie.suggester.api.domain.actors

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import com.bhameyie.suggester.api.domain.protocols.CityLookupResult.{MultipleFound, NoMatchFound, SingleMatch}
import com.bhameyie.suggester.api.domain.protocols._
import com.bhameyie.suggester.api.domain.protocols.SearchCriteria.{SearchByQuery, SearchByQueryAndCoordinates}
import org.mongodb.scala.MongoDatabase
import org.mongodb.scala.bson.conversions.Bson


object SearchController {
  def apply(): Props = ???
}

class SearchController(ranker: ActorRef, cityFinder: ActorRef) extends Actor with ActorLogging {
  override def receive: Receive = {
    case FindCity(name, None) =>
      log.debug(s"got request to find city $name without coordinates")

      cityFinder ! SearchByQuery(sender(), name)


    case FindCity(name, Some(coordinates)) =>
      log.debug(s"got request to find city $name with coordinates $coordinates")

      cityFinder ! SearchByQueryAndCoordinates(sender(), name, coordinates)


    case res: NoMatchFound =>
      log.debug(s"no match found on ${res.searchedName}")

      res.originator ! SearchResult.Nothing

    case res: SingleMatch =>
      log.debug(s"Single match found on ${res.searchedName}")

      res.originator ! SearchResult.Matches(List(RankedCityRecord(1.0, res.cityRecord)))

    case res: MultipleFound =>
      log.debug(s"Found multiple records matching ${res.searchedName}")

      ranker ! ComputeCityRankings(res.originator, res.searchedName, res.records)
  }
}


class MatchRanker extends Actor with ActorLogging{
  override def receive: Receive = {
    case request:ComputeCityRankings=>

  }
}