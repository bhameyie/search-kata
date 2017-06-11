package com.bhameyie.suggester.api.rest.dtos

import akka.http.scaladsl.model.StatusCode
import com.bhameyie.suggester.api.domain.protocols.SearchResult
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._

/**
  * Created by bhameyie on 6/10/17.
  */

case class PresentableSuggestion(
                                  name: String,
                                  latitude: String,
                                  longitude: String,
                                  score: Double
                                )


object PresentableSuggestions {
  def apply(searchResult: SearchResult): Seq[PresentableSuggestion] = ???
}