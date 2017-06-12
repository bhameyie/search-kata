package com.bhameyie.suggester.importer


object RecordConverter {
  import com.bhameyie.suggester.database.{DatabaseCityRecord, DbCoordinate}

  def convert(codeCache:Map[String,String],fileCityRecord: FileCityRecord): Seq[DatabaseCityRecord] = {

    val realCode = fileCityRecord.admin1Code match {
      case Some(value) => codeCache.get(s"${fileCityRecord.countryCode}.$value")
      case None => None
    }
    val principal = DatabaseCityRecord(
      fileCityRecord.geonameId, fileCityRecord.name, fileCityRecord.countryCode,
      DbCoordinate(List(fileCityRecord.longitude, fileCityRecord.latitude)), realCode
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
