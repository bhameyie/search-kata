package com.bhameyie.suggester.api.rest

import cats.data.Validated
import com.bhameyie.suggester.api.domain.protocols.FindCity
import com.bhameyie.suggester.api.rest.dtos.SearchRequest

/**
  * Created by bhameyie on 6/10/17.
  */
object Validator {

  def validate(searchRequest: SearchRequest):Validated[ValidationFailure,FindCity]={
    ???
  }

}


