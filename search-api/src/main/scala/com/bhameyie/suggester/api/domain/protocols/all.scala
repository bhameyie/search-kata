package com.bhameyie.suggester.api.domain.protocols

import akka.actor.ActorRef

case class Coordinates(longitude: Double, latitude: Double)

case class GeoSpatialId(id: Int)

case class RankedCityRecord(score: Double, record: CityRecord)

case class CityRecord(spatialId: GeoSpatialId, name: String, country: String,
                      coordinates: Coordinates, adminRegion: Option[String]) {

  def formattedName:String = List(name,adminRegion.orNull,country).mkString(", ")

  def canEqual(a: Any): Boolean = a.isInstanceOf[CityRecord]

  override def equals(that: Any): Boolean = that match {
    case that: CityRecord => that.canEqual(this) && this.hashCode == that.hashCode
    case _ => false
  }

  override def hashCode: Int = {
    formattedName.hashCode
  }
}

case class UnanticipatedFailure(originator: ActorRef, reason: String, stage: PipelineStage)
