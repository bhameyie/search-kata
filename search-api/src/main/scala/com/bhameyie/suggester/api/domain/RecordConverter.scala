package com.bhameyie.suggester.api.domain

import com.bhameyie.suggester.api.domain.protocols.{CityRecord, Coordinates, GeoSpatialId}
import com.bhameyie.suggester.database.DatabaseCityRecord

object RecordConverter {
  def convert(record: DatabaseCityRecord): CityRecord = {
    CityRecord(
      GeoSpatialId(record.spatialId),
      record.name,
      record.country,
      Coordinates(record.location.coordinates.head, record.location.coordinates(1)),
      record.adminRegion
    )
  }
}
