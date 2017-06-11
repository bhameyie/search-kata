package com.bhameyie.suggester.database

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




