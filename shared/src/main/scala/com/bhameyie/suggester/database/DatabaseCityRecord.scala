package com.bhameyie.suggester.database

/**
  * Coordinates. First coordinate element is the longitude, the next one is the latitude
  * @param coordinates
  * @param `type`
  */
case class DbCoordinate(
                         coordinates: List[Double],
                           `type`: String="Point"
                       )


/**
  * City database object
  * @param spatialId
  * @param name
  * @param country
  * @param location
  * @param adminRegion
  */
case class DatabaseCityRecord(spatialId:Int,
                              name:String,
                             country:String,
                              location:DbCoordinate,
                             adminRegion:Option[String])

object DatabaseCityRecord{

  import org.bson.codecs.configuration.CodecRegistries.fromProviders
  import org.mongodb.scala.bson.codecs.Macros._

  val codecs= List(fromProviders(classOf[DatabaseCityRecord]), fromProviders(classOf[DbCoordinate]))
}



