package com.bhameyie.suggester.api.domain

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import com.bhameyie.suggester.api.domain.protocols.CityLookupResult._
import com.bhameyie.suggester.api.domain.protocols.SearchCriteria.{SearchByQuery, SearchByQueryAndCoordinates}
import com.bhameyie.suggester.api.domain.protocols._


object SearchController {
  def apply(ranker: ActorRef, cityFinder: ActorRef): Props =
    Props(new SearchController(ranker, cityFinder))
}

/**
  * Actor responsible for coordinate the suggestion retrieval operation
  * @param ranker ranker actor
  * @param cityFinder finder actor
  */
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

    case res: MultipleFound =>
      log.debug(s"Found multiple records matching ${res.searchedName}")

      ranker ! ComputeCityRankings(res.originator, res.searchedName, res.records)

    case res: SingleMatch =>
      log.debug(s"Single match found on ${res.searchedName}")

      res.originator ! SearchResult.Matches(List(RankedCityRecord(1.0, res.cityRecord)))

    case res: RankedResults =>
      log.debug(s"Found multiple ranked results matching ${res.searchedName}")

      res.originator ! SearchResult.Matches(res.records)
  }
}
