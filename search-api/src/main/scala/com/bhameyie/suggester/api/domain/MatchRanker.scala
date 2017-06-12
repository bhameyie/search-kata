package com.bhameyie.suggester.api.domain

import akka.actor.{Actor, ActorLogging, Props}
import com.bhameyie.suggester.api.domain.protocols.CityLookupResult.RankedResults
import com.bhameyie.suggester.api.domain.protocols.{ComputeCityRankings, RankedCityRecord}

object MatchRanker {
  def apply(): Props = Props[MatchRanker]()
}

/**
  * Actor responsible for assigning a score city suggestion
  */
class MatchRanker extends Actor with ActorLogging {

  import org.apache.commons.lang3.StringUtils

  override def receive: Receive = {
    case request: ComputeCityRankings =>

      val rankedCityRecords = request.records.par.map { e =>

        // using Jaro-Winkler distance from apache commons.
        val score = StringUtils.getJaroWinklerDistance(request.searchedName, e.name)

        RankedCityRecord(score, e)
      }.toList

      sender() ! RankedResults(request.originator, request.searchedName, rankedCityRecords)
  }
}
