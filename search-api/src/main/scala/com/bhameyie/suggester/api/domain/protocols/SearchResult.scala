package com.bhameyie.suggester.api.domain.protocols

/**
  * Created by bhameyie on 6/10/17.
  */
sealed trait SearchResult

object SearchResult {

  case object Nothing extends SearchResult

  case class Matches(records: Seq[RankedCityRecord]) extends SearchResult

}

