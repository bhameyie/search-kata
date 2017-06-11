package com.bhameyie.suggester.api.domain.protocols

/**
  * Created by bhameyie on 6/10/17.
  */
object PipelineStage{
  case object CityLookup
  case object RankScoring
}
sealed trait PipelineStage
