package com.bhameyie.suggester.api.rest.dtos

import com.bhameyie.suggester.api.domain.protocols.SearchResult
import com.bhameyie.suggester.api.domain.protocols.SearchResult.{Matches, Nothing}

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
  def apply(searchResult: SearchResult): Seq[PresentableSuggestion] = {
    searchResult match {
      case Nothing => Seq()
      case Matches(records) =>
        records.map(e => PresentableSuggestion(e.record.name,
          e.record.coordinates.latitude.toString,
          e.record.coordinates.longitude.toString,
          e.score))
    }
  }
}