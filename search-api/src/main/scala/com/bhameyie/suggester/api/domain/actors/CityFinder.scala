package com.bhameyie.suggester.api.domain.actors

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import com.bhameyie.suggester.api.domain.protocols.CityLookupResult.{MultipleFound, NoMatchFound, SingleMatch}
import com.bhameyie.suggester.api.domain.protocols.SearchCriteria.{SearchByQuery, SearchByQueryAndCoordinates}
import org.mongodb.scala.MongoDatabase
import org.mongodb.scala.bson.conversions.Bson

object CityFinder {
  def apply(mongoDatabase: MongoDatabase): Props = ???
}

class CityFinder(mongoDatabase: MongoDatabase) extends Actor with ActorLogging {

  import akka.pattern.pipe
  import org.mongodb.scala.model.Filters
  import context.dispatcher

  private val recordsCollection = mongoDatabase.getCollection[DatabaseCityRecord]("city_records")

  private def search(q: Bson, originator: ActorRef, nameQuery: String) = {
    val recs = recordsCollection.find(q)
      .limit(15)
      .toFuture()
      .map(e => e.map(x => RecordConverter.convert(x)).distinct)
      .map {
        case Seq(x) => SingleMatch(originator, nameQuery, x)
        case e@Seq(x, xs@_*) => MultipleFound(originator, nameQuery, e)
        case Seq() => NoMatchFound(originator, nameQuery)
      }

    pipe(recs) to sender()
  }

  override def receive: Receive = {
    case criteria: SearchByQuery =>

      val q: Bson = Filters.regex("name", s"/^${criteria.nameQuery}/")
      search(q, criteria.originator, criteria.nameQuery)


    case criteria: SearchByQueryAndCoordinates =>
      val q = Filters.and(Filters.regex("name", s"/^${criteria.nameQuery}/"),
        Filters.near("location", criteria.coordinates.longitude, criteria.coordinates.latitude)
      )

      search(q, criteria.originator, criteria.nameQuery)
  }
}
