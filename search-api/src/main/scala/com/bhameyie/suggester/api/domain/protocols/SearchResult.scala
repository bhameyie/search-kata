package com.bhameyie.suggester.api.domain.protocols

sealed trait SearchResult

object SearchResult {

  case object Nothing extends SearchResult

  case class Matches(records: Seq[RankedCityRecord]) extends SearchResult

}

