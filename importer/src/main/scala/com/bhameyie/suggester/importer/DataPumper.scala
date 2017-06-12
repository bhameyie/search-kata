package com.bhameyie.suggester.importer

import scala.concurrent.{ExecutionContext, Future}

/**
  * Loads records into the database
  */
object DataPumper {

  import akka.stream.ActorMaterializer
  import com.bhameyie.suggester.database.DatabaseCityRecord
  import org.mongodb.scala._

  /**
    * Add/Replace city data
    * @param recordsCollection
    * @param records
    * @param materializer
    * @param executionContext
    * @return
    */
  def pump(recordsCollection: MongoCollection[DatabaseCityRecord])
          (records: Seq[DatabaseCityRecord])
          (implicit materializer: ActorMaterializer,
           executionContext: ExecutionContext): Future[Completed] = {

    //delete existing data before loading
    recordsCollection.deleteMany(Document("spatialId" -> Document("$in" -> records.map(_.spatialId))))
      .toFuture.flatMap(_ => recordsCollection.insertMany(records).toFuture)
  }
}
