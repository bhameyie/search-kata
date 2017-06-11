package com.bhameyie.suggester.api.domain.protocols

sealed trait SearchCriteria

object SearchCriteria {

  case class SearchByQuery(query: String) extends SearchCriteria

  case class SearchByQueryAndCoordinates(query: String, coordinates: Coordinates) extends SearchCriteria

}