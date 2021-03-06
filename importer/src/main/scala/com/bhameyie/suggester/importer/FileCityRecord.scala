package com.bhameyie.suggester.importer

object FileCityRecord {
  /**
    * Reads a 19 column tsv row
    * @param tsv
    * @return
    */
  def apply(tsv: Array[String]): FileCityRecord = {

    FileCityRecord(
      tsv.head.toInt,
      tsv(1),
      tsv(2),
      OptionalStringParser.parse(tsv(3)),
      tsv(4).toDouble,
      tsv(5).toDouble,
      tsv(6),
      tsv(7),
      tsv(8),
      OptionalStringParser.parse(tsv(9)),
      OptionalStringParser.parse(tsv(10)),
      OptionalStringParser.parse(tsv(11)),
      OptionalStringParser.parse(tsv(12)),
      OptionalStringParser.parse(tsv(13)),

      OptionalStringParser.parse(tsv(14)).map(_.toLong),
      OptionalStringParser.parse(tsv(15)).map(_.toInt),

      tsv(16).toInt,
      tsv(17),
      tsv(18)
    )
  }
}

/**
  * City record found from tsv
  * @param geonameId
  * @param name
  * @param asciiName
  * @param alternateNames
  * @param latitude
  * @param longitude
  * @param featureClass
  * @param featureCode
  * @param countryCode
  * @param cc2
  * @param admin1Code
  * @param admin2Code
  * @param admin3Code
  * @param admin4Code
  * @param population
  * @param elevation
  * @param dem
  * @param timezone
  * @param modificationDate
  */
case class FileCityRecord(
                           geonameId: Int,
                           name: String,
                           asciiName: String,
                           alternateNames: Option[String],
                           latitude: Double,
                           longitude: Double,
                           featureClass: String,
                           featureCode: String,
                           countryCode: String,
                           cc2: Option[String],
                           admin1Code: Option[String],
                           admin2Code: Option[String],
                           admin3Code: Option[String],
                           admin4Code: Option[String],
                           population: Option[Long],
                           elevation: Option[Int],
                           dem: Int,
                           timezone: String,
                           modificationDate: String
                         )
