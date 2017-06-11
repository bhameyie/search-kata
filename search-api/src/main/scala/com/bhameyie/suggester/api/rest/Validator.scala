package com.bhameyie.suggester.api.rest

import cats.data.Validated
import cats.data.Validated.{Invalid, Valid}
import com.bhameyie.suggester.api.domain.protocols.{Coordinates, FindCity}
import com.bhameyie.suggester.api.rest.dtos.SearchRequest
import scala.util.Try

object Validator {

  def validate(searchRequest: SearchRequest): Validated[ValidationFailure, FindCity] = {
    searchRequest match {

      case SearchRequest(q, _, _) if q.replaceAll("\\s", "").isEmpty => Invalid(ValidationFailure.QueryIsEmpty)

      case SearchRequest(_, Some(_), None) => Invalid(ValidationFailure.MissingCoordinatePoint)

      case SearchRequest(_, None, Some(_)) => Invalid(ValidationFailure.MissingCoordinatePoint)

      case SearchRequest(_, Some(long), Some(lat)) if Try(long.toDouble).isFailure || Try(lat.toDouble).isFailure=> Invalid(ValidationFailure.NotANumber)

      case SearchRequest(_, Some(long), _) if long.toDouble > 180.0 || long.toDouble < -180.0 =>
        Invalid(ValidationFailure.OutOfBoundCoordinates)

      case SearchRequest(_, Some(_), Some(lat)) if lat.toDouble > 90.0 || lat.toDouble < -90.0 =>
        Invalid(ValidationFailure.OutOfBoundCoordinates)

      case SearchRequest(q, None, None) => Valid(FindCity(q, None))

      case SearchRequest(q, Some(long), Some(lat)) => Valid(FindCity(q, Some(Coordinates(long.toDouble, lat.toDouble))))
    }

  }

}


