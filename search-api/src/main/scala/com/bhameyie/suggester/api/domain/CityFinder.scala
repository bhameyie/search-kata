package com.bhameyie.suggester.api.domain

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import com.bhameyie.suggester.api.domain.protocols.CityLookupResult.{MultipleFound, NoMatchFound, SingleMatch}
import com.bhameyie.suggester.api.domain.protocols.Coordinates
import com.bhameyie.suggester.api.domain.protocols.SearchCriteria.{SearchByQuery, SearchByQueryAndCoordinates}
import com.bhameyie.suggester.database.{Collections, DatabaseCityRecord}
import org.mongodb.scala.MongoDatabase
import org.mongodb.scala.bson.BsonArray
import org.mongodb.scala.bson.collection.immutable.Document
import org.mongodb.scala.bson.conversions.Bson

object CityFinder {
  def apply(mongoDatabase: MongoDatabase): Props = Props(new CityFinder(mongoDatabase))
}

class CityFinder(mongoDatabase: MongoDatabase) extends Actor with ActorLogging {

  import akka.pattern.pipe
  import context.dispatcher

  private val recordsCollection = {
    val coll = mongoDatabase.getCollection[DatabaseCityRecord](Collections.cityRecords)
    coll.createIndex(Document("location" -> "2dsphere")).toFuture
    coll
  }

  private def search(q: Bson, originator: ActorRef, nameQuery: String) = {
    log.debug(s"looking up documents from query $q")
    val recs = recordsCollection.find(q)
      .limit(15)
      .toFuture()
      .map(e => e.map(x => RecordConverter.convert(x)).distinct)
      .map {
        case Seq(x) =>
          log.debug(s"single match $x")
          SingleMatch(originator, nameQuery, x)

        case e@Seq(x, xs@_*) =>
          log.debug(s"multi match")
          MultipleFound(originator, nameQuery, e)

        case Seq() =>
          log.debug("no match found")
          NoMatchFound(originator, nameQuery)
      }

    pipe(recs) to sender()
  }

  private def nameMongoQuery(name: String) = {
    Document("name" -> Document("$regex" -> s".*$name.*", "$options" -> "i"))
  }

  private def coordinateQuery(coordinates: Coordinates) = {
    Document(
      "location" ->
        Document("$near" ->
          Document(
            "$geometry" ->
              Document("type" -> "Point",
                "coordinates" -> BsonArray(coordinates.longitude, coordinates.latitude))
          )
        )
    )
  }

  override def receive: Receive = {
    case criteria: SearchByQuery =>

      val q: Bson = nameMongoQuery(criteria.nameQuery)
      search(q, criteria.originator, criteria.nameQuery)


    case criteria: SearchByQueryAndCoordinates =>
      val q = Document("$and" -> BsonArray(nameMongoQuery(criteria.nameQuery),
        coordinateQuery(criteria.coordinates)))


      search(q, criteria.originator, criteria.nameQuery)
  }
}