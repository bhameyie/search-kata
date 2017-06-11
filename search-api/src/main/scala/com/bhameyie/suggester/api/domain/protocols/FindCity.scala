package com.bhameyie.suggester.api.domain.protocols

import com.bhameyie.suggester.api.domain.protocols.Coordinates

/**
  * Created by bhameyie on 6/10/17.
  */
case class FindCity(name:String, coordinates: Option[Coordinates])
