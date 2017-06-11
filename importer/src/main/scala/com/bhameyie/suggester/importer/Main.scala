package com.bhameyie.suggester.importer

import scala.util.{Failure, Success}

object Main extends App  {

  import org.mongodb.scala._
  import akka.actor.ActorSystem
  import akka.stream.scaladsl._
  import akka.stream.{ActorMaterializer, ActorMaterializerSettings, Supervision}
  import akka.util.ByteString
  import cats.data.Validated.{Invalid, Valid}
  import com.bhameyie.suggester.database.{ApplicationDatabase, Collections, DatabaseCityRecord}
  import com.mongodb.client.model.IndexOptions
  import com.typesafe.config.ConfigFactory
  import com.typesafe.scalalogging.Logger
  import org.mongodb.scala.model.Filters._
  import org.mongodb.scala.result.DeleteResult

  private val conf = ConfigFactory.load()
  private val recordsCollection = {
    val mongoDatabase = ApplicationDatabase(conf)

    val coll = mongoDatabase.getCollection[DatabaseCityRecord](Collections.cityRecords)
    coll.createIndex(org.mongodb.scala.model.Indexes.geo2d("location"))
    coll.createIndex(org.mongodb.scala.model.Indexes.text("spatialId"), new IndexOptions().unique(true))
    coll
  }

  private implicit val system = {
    val s = ActorSystem("Suggester")
    sys.addShutdownHook(s.terminate())
    s
  }

  private val logger: Logger = Logger("Processor")

  private val validityLogging = ExecutionLogger.logValidationFailures(logger) _

  private val decider: Supervision.Decider = _ => Supervision.Stop

  private implicit val materializer = ActorMaterializer(
    ActorMaterializerSettings(system).withSupervisionStrategy(decider))

  private implicit val executionContext = system.dispatcher


  Validator.validate(args) match {
    case Invalid(e) => validityLogging(e)

    case Valid(path) =>
      FileIO.fromPath(path)
        .via(Framing.delimiter(ByteString("\n"), Int.MaxValue))
        .map(_.utf8String)
        .map { line =>
          //todo: should worry about tsv with less than 19 columns
          val tsvSplit = line.split('\t')
          val rec = FileCityRecord(tsvSplit)
          RecordConverter.convert(rec).toList
        }
        .mapAsync(4){ e =>
            recordsCollection.deleteMany(Document("spatialId"-> Document("$in" -> e.map(_.spatialId))))
              .toFuture.flatMap(x=> recordsCollection.insertMany(e).toFuture)

        }
        .to(Sink.ignore).run().onComplete {

        case Failure(exception) =>
          logger.error(s"An error occurred while processing the stream. ${exception.getMessage}", exception)
        case Success(_) =>

          logger.debug("COMPLETED SUCCESSFULLY")
          system.terminate()
      }
  }
}


