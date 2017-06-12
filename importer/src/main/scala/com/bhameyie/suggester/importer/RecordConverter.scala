package com.bhameyie.suggester.importer

/**
  * Converts a single FileCityRecord into multiple based found names
  */
object RecordConverter {
  import com.bhameyie.suggester.database.{DatabaseCityRecord, DbCoordinate}

  /**
    * Perform the conversion
    * @param codeCache the admin1code mapping
    * @param fileCityRecord the city record found in the file
    * @return a collection of database records that can be dumped into the database
    */
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
