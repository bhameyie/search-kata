package com.bhameyie.suggester.api.domain.protocols

import akka.actor.ActorRef

case class ComputeCityRankings(originator:ActorRef, searchedName:String, records:Seq[CityRecord])
