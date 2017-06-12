package com.bhameyie.suggester.importer

import scala.concurrent.{ExecutionContext, Future}

object DataPumper {

  import akka.stream.ActorMaterializer
  import com.bhameyie.suggester.database.DatabaseCityRecord
  import org.mongodb.scala._

  def pump(recordsCollection: MongoCollection[DatabaseCityRecord])
          (records: Seq[DatabaseCityRecord])
          (implicit materializer: ActorMaterializer,
           executionContext: ExecutionContext): Future[Completed] = {
    recordsCollection.deleteMany(Document("spatialId" -> Document("$in" -> records.map(_.spatialId))))
      .toFuture.flatMap(_ => recordsCollection.insertMany(records).toFuture)
  }
}
