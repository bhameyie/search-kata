package com.bhameyie.suggester.api.domain.protocols

import akka.actor.ActorRef

/**
  * Created by bhameyie on 6/10/17.
  */
case class ComputeCityRankings(originator:ActorRef, searchedName:String, records:Seq[CityRecord])
