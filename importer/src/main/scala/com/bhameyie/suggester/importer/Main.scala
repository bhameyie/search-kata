package com.bhameyie.suggester.importer

object Main {

  import scala.concurrent.Await
  import scala.language.postfixOps

  import akka.actor.ActorSystem
  import akka.stream.scaladsl._
  import akka.stream.{ActorMaterializer, ActorMaterializerSettings, Supervision}
  import akka.util.ByteString
  import com.bhameyie.suggester.database.{ApplicationDatabase, Collections, DatabaseCityRecord}
  import com.typesafe.scalalogging.Logger
  import org.mongodb.scala._
  import scala.concurrent.duration._
  import scala.util.{Failure, Success}

  private val logger: Logger = Logger("Processor")

  private val decider: Supervision.Decider = _ => Supervision.Stop

  def main(args: Array[String]): Unit = {
    ParameterParser().parse(args, RunParameters()).foreach(run)
  }

  def run(param: RunParameters): Unit = {
    implicit val system = {
      val s = ActorSystem("Suggester")
      sys.addShutdownHook(s.terminate())
      s
    }

    implicit val materializer = ActorMaterializer(
      ActorMaterializerSettings(system).withSupervisionStrategy(decider))

    implicit val executionContext = system.dispatcher

    val adminCodeCache = AdminCodeCache(param.adminCodeFile.toPath)

    val recordsCollection: MongoCollection[DatabaseCityRecord] = {
      val mongoDatabase = ApplicationDatabase(param.mongoUrl, param.mongoDb)

      val coll = mongoDatabase.getCollection[DatabaseCityRecord](Collections.cityRecords)
      coll.createIndex(Document("location" -> "2dsphere")).toFuture
      coll
    }

    val pumper = DataPumper.pump(recordsCollection) _
    val processCity = LineProcessor.process(adminCodeCache) _

    FileIO.fromPath(param.dataFile.toPath)
      .via(Framing.delimiter(ByteString("\n"), Int.MaxValue))
      .map(_.utf8String)
      .map(processCity)
      .mapAsync(4)(pumper)
      .to(Sink.ignore).run().onComplete {

      case Failure(exception) =>
        logger.error(s"An error occurred while processing the stream. ${exception.getMessage}", exception)
      case Success(_) =>

        logger.debug("COMPLETED SUCCESSFULLY")
        Await.ready(system.terminate(), 10 seconds)

    }
  }
}

