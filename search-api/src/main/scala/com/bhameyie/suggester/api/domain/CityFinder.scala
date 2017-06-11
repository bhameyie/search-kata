package com.bhameyie.suggester.api.domain

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import com.bhameyie.suggester.api.domain.protocols.CityLookupResult.{MultipleFound, NoMatchFound, SingleMatch}
import com.bhameyie.suggester.api.domain.protocols.SearchCriteria.{SearchByQuery, SearchByQueryAndCoordinates}
import com.bhameyie.suggester.database.{ApplicationDatabase, Collections, DatabaseCityRecord}
import com.mongodb.client.model.IndexOptions
import org.mongodb.scala.MongoDatabase
import org.mongodb.scala.bson.conversions.Bson
import org.mongodb.scala.model.Indexes.{geo2d, text}

object CityFinder {
  def apply(mongoDatabase: MongoDatabase): Props = Props(new CityFinder(mongoDatabase))
}

class CityFinder(mongoDatabase: MongoDatabase) extends Actor with ActorLogging {

  import akka.pattern.pipe
  import context.dispatcher
  import org.mongodb.scala.model.Filters

  private val recordsCollection = {
    val coll = mongoDatabase.getCollection[DatabaseCityRecord](Collections.cityRecords)
    coll.createIndex(geo2d("location"))
    coll.createIndex(text("spatialId"), new IndexOptions().unique(true))
    coll
  }

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
      val q = Filters.and(Filters.regex("name", s"/${criteria.nameQuery}/"),
        Filters.near("location", criteria.coordinates.longitude, criteria.coordinates.latitude)
      )

      search(q, criteria.originator, criteria.nameQuery)
  }
}