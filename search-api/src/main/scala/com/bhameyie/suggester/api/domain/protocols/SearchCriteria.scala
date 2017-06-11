package com.bhameyie.suggester.api.domain.protocols

import akka.actor.ActorRef

sealed trait SearchCriteria

object SearchCriteria {

  case class SearchByQuery(originator:ActorRef, nameQuery: String) extends SearchCriteria

  case class SearchByQueryAndCoordinates(originator:ActorRef, nameQuery: String, coordinates: Coordinates) extends SearchCriteria

}
