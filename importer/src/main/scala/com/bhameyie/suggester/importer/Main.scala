package com.bhameyie.suggester.importer


object Main extends App {

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
  private implicit val system = {
    val s = ActorSystem("Suggester")
    sys.addShutdownHook(s.terminate())
    s
  }

  private val logger: Logger = Logger("Processor")

  private val validityLogging = ExecutionLogger.logValidationFailures(logger) _

  private val decider: Supervision.Decider = _ => Supervision.Restart

  private implicit val materializer = ActorMaterializer(
    ActorMaterializerSettings(system).withSupervisionStrategy(decider))

  private implicit val executionContext = system.dispatcher
  private val recordsCollection = {
    val mongoDatabase = ApplicationDatabase(conf)
    val coll = mongoDatabase.getCollection[DatabaseCityRecord](Collections.cityRecords)
    coll.createIndex(org.mongodb.scala.model.Indexes.geo2d("location"))
    coll.createIndex(org.mongodb.scala.model.Indexes.text("spatialId"), new IndexOptions().unique(true))
    coll
  }

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
        .map(e => (recordsCollection.deleteMany(in("spatialId", e.map(_.spatialId))), e))
        .runForeach { e =>
          val (obs, recs) = e

          obs.subscribe((res: DeleteResult) => if (res.wasAcknowledged()) {
            recordsCollection.insertMany(recs)
          })
        }
  }
}


