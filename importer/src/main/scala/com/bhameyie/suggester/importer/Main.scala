package com.bhameyie.suggester.importer

object Main {

  import akka.actor.ActorSystem
  import akka.stream.scaladsl._
  import akka.stream.{ActorMaterializer, ActorMaterializerSettings, Supervision}
  import akka.util.ByteString
  import cats.data.Validated.{Invalid, Valid}
  import com.bhameyie.suggester.database.{ApplicationDatabase, Collections, DatabaseCityRecord}
  import com.typesafe.config.ConfigFactory
  import com.typesafe.scalalogging.Logger
  import org.mongodb.scala._

  import scala.util.{Failure, Success}

  private val conf = ConfigFactory.load()
  private val recordsCollection: MongoCollection[DatabaseCityRecord] = {
    val mongoDatabase = ApplicationDatabase(conf)

    val coll = mongoDatabase.getCollection[DatabaseCityRecord](Collections.cityRecords)
    coll.createIndex(Document("location" -> "2dsphere")).toFuture
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
  private val pumper = DataPumper.pump(recordsCollection) _

  def main(args: Array[String]): Unit = {

    Validator.validate(args) match {
      case Invalid(e) => validityLogging(e)

      case Valid(path) =>

        FileIO.fromPath(path)
          .via(Framing.delimiter(ByteString("\n"), Int.MaxValue))
          .map(_.utf8String)
          .map(LineProcessor.process)
          .mapAsync(4)(pumper)
          .to(Sink.ignore).run().onComplete {

          case Failure(exception) =>
            logger.error(s"An error occurred while processing the stream. ${exception.getMessage}", exception)
          case Success(_) =>

            logger.debug("COMPLETED SUCCESSFULLY")
            system.terminate()
        }
    }
  }
}

