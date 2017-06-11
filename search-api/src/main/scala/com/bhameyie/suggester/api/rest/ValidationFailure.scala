package com.bhameyie.suggester.api.rest

/**
  * Created by bhameyie on 6/10/17.
  */
sealed trait ValidationFailure

object ValidationFailure{
  case object QueryIsEmpty extends ValidationFailure{
    override def toString()="The search query must have a value"
  }

  case object OutOfBoundCoordinates extends ValidationFailure{
    override def toString: String = "The specified coordinates are out of bound"
  }


  case object MissingCoordinatePoint extends ValidationFailure{
    override def toString: String = "Both the 'long' and 'lat' must be supplied when one is supplied"
  }

}
