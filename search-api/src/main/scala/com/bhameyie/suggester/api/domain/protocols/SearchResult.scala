package com.bhameyie.suggester.api.domain.protocols

/**
  * Created by bhameyie on 6/10/17.
  */
sealed trait SearchResult

object SearchResult {

  case object NoMatchFound extends SearchResult

  case class SingleMatch(cityRecord: CityRecord) extends SearchResult

  case class MultipleFound() extends SearchResult

}
