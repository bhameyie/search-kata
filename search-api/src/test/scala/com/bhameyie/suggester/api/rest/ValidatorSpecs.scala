package com.bhameyie.suggester.api.rest

import com.bhameyie.suggester.api.domain.protocols._
import com.bhameyie.suggester.api.rest.dtos.SearchRequest
import org.scalatest.prop.Checkers
import org.scalatest.{Matchers, WordSpec}

class ValidatorSpecs extends WordSpec with Matchers with Checkers {

  "For invalid requests, Validate" should {
    "reject missing long coordinates" in {
      val req = SearchRequest("bla", None, Some("34"))
      val res = Validator.validate(req)

      res.isInvalid should equal(true)

      res.toEither.left.get should equal(ValidationFailure.MissingCoordinatePoint)
    }

    "reject missing lat coordinates" in {
      val req = SearchRequest("bla", Some("34"), None)
      val res = Validator.validate(req)

      res.isInvalid should equal(true)

      res.toEither.left.get should equal(ValidationFailure.MissingCoordinatePoint)
    }

    "reject out of lower bound lat" in {
      val req = SearchRequest("bla", Some("34"), Some("-91.999"))
      val res = Validator.validate(req)

      res.isInvalid should equal(true)

      res.toEither.left.get should equal(ValidationFailure.OutOfBoundCoordinates)

    }

    "reject out of upper bound lat" in {
      val req = SearchRequest("bla", Some("34"), Some("90.0001"))
      val res = Validator.validate(req)

      res.isInvalid should equal(true)

      res.toEither.left.get should equal(ValidationFailure.OutOfBoundCoordinates)

    }

    "reject out of lower bound long" in {
      val req = SearchRequest("bla", Some("-181.0001"), Some("-90"))
      val res = Validator.validate(req)

      res.isInvalid should equal(true)

      res.toEither.left.get should equal(ValidationFailure.OutOfBoundCoordinates)

    }

    "reject out of upper bound long" in {
      val req = SearchRequest("bla", Some("180.0001"), Some("90"))
      val res = Validator.validate(req)

      res.isInvalid should equal(true)

      res.toEither.left.get should equal(ValidationFailure.OutOfBoundCoordinates)

    }

    "rejects empty query" in {

      val req = SearchRequest("           ", None, None)

      val res = Validator.validate(req)
      res.isInvalid should equal(true)

      res.toEither.left.get should equal(ValidationFailure.QueryIsEmpty)

    }
  }

  "For Valid requests, Validate" should {
    "accept SearchRequests with just the name" in {

      val req = SearchRequest("bla", None, None)

      val res = Validator.validate(req)

      res.isValid should equal(true)

      res.toOption.get should equal(FindCity("bla", None))
    }

    "accept SearchRequests with name and valid coordinates" in {

      val req = SearchRequest("bla", Some("45.0"), Some("0.4354"))

      val res = Validator.validate(req)

      res.isValid should equal(true)

      res.toOption.get should equal(FindCity("bla", Some(Coordinates(45.0, 0.4354))))
    }
  }

}
