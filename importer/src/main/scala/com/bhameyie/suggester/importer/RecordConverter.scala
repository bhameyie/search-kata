package com.bhameyie.suggester.importer

import com.bhameyie.suggester.database.{DatabaseCityRecord, DbCoordinate}

object RecordConverter {

  def convert(fileCityRecord: FileCityRecord): Seq[DatabaseCityRecord] = {

    val principal = DatabaseCityRecord(
      fileCityRecord.geonameId, fileCityRecord.name, fileCityRecord.countryCode,
      DbCoordinate(List(fileCityRecord.longitude, fileCityRecord.latitude)), fileCityRecord.admin1Code
    )

    val secondary = principal.copy(name = fileCityRecord.asciiName)

    val combinedSet = Seq(principal, secondary)

    fileCityRecord.alternateNames
      .map { e =>
        e.split(',').map(n => principal.copy(name = n))
      }
      .getOrElse(Array()) ++ combinedSet
  }
}
