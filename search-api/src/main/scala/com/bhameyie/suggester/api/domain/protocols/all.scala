package com.bhameyie.suggester.api.domain.protocols

case class Coordinates(longitude: Double, latitude: Double)

case class GeoSpatialId(id: String)

case class CityRecord(spatialId: GeoSpatialId, name: String, country: String,
                      coordinates: Coordinates, adminRegion: Option[String])

