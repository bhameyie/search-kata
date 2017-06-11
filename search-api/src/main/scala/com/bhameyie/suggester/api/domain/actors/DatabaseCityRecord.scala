package com.bhameyie.suggester.api.domain.actors

import com.bhameyie.suggester.api.domain.protocols.CityRecord


/**
  * Created by bhameyie on 6/10/17.
  */
case class DatabaseCityRecord(spatialId:String)


object RecordConverter{
  def convert(record:DatabaseCityRecord):CityRecord = ???
}