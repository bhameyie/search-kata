package com.bhameyie.suggester.api.domain.protocols

import akka.actor.ActorRef


sealed trait CityLookupResult

object CityLookupResult {

  case class NoMatchFound(originator: ActorRef, searchedName: String) extends CityLookupResult

  case class RankedResults(originator: ActorRef, searchedName: String, records: Seq[RankedCityRecord]) extends CityLookupResult

  case class SingleMatch(originator: ActorRef, searchedName: String, cityRecord: CityRecord) extends CityLookupResult

  case class MultipleFound(originator: ActorRef, searchedName: String, records: Seq[CityRecord]) extends CityLookupResult

}
