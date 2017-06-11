package com.bhameyie.suggester.database

import org.bson.types.ObjectId
import org.mongodb.scala.bson.ObjectId

/**
  * Created by bhameyie on 6/10/17.
  */


//first coordinate element is the longitude, the next one is the latitude
case class DbCoordinate(
                         coordinates: List[Double],
                           `type`: String="Point"
                       )


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



