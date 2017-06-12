package com.bhameyie.suggester.importer

import java.io.File

import com.bhameyie.suggester.database.{ApplicationDatabase, Collections, DatabaseCityRecord}
import com.typesafe.config.ConfigFactory
import org.mongodb.scala.Document
import org.scalatest.prop.Checkers
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpec}

import scala.concurrent.Await

class LineProcessorSpecs extends WordSpec with Matchers with Checkers {

  private val cache = Map("CA.02" -> "Ontario", "US.CA" -> "California")
  private val sut = LineProcessor.process(cache) _

  "processing line" should {
    "produce 2 results when no alternate names" in {
      val line = List("5881791", "Abbotsford", "Abbotsfordius", "", "49.05798", "-122.25257", "P",
        "PPL", "CA","", "02","","","", "5957659", "151683", "114", "America/Vancouver", "2013-04-22").mkString("\t")

      val res = sut(line)

      res.size shouldEqual 2
      res.map(_.name) shouldEqual List("Abbotsford", "Abbotsfordius")

      val unified = res.map(e => e.copy(name = "bla"))

      unified.head shouldEqual unified(1)
    }

    "produce 4 results when 2 alternate names present" in {

      val line = List("5881791", "Abbotsford", "Abbotsfordham", "Abbotsfordy,YXX", "49.05798", "-122.25257", "P",
        "PPL", "CA","", "02","","","", "5957659", "151683", "114", "America/Vancouver", "2013-04-22").mkString("\t")

      val res = sut(line)

      res.size shouldEqual 4
      res.map(_.name) shouldEqual List("Abbotsfordy", "YXX", "Abbotsford", "Abbotsfordham")

      val unified = res.map(e => e.copy(name = "bla"))
      unified.head shouldEqual unified(1)
      unified.head shouldEqual unified(2)
      unified.head shouldEqual unified(3)
    }
  }
}

class AppIntegrationSpecs extends WordSpec with Matchers with Checkers with BeforeAndAfterAll {
  import scala.concurrent.duration._
  private val currentDir = System.getProperty("user.dir")

  private val conf = ConfigFactory.load()
  private val db = ApplicationDatabase(conf)

  private val citiesColl = {
    val coll = db.getCollection[DatabaseCityRecord](Collections.cityRecords)
    coll.createIndex(Document("location" -> "2dsphere")).toFuture
    coll
  }

  override def beforeAll(): Unit = {
    Await.result(citiesColl.drop().toFuture, 10 seconds)
  }

  "run" should{

    "load tsv" in{

      val param = RunParameters(new File("data/small.tsv"),new File("data/small_codes.tsv"),
        "mongodb://localhost","suggester")
      Main.run(param)

      Thread.sleep(3000)

      val res= Await.result( citiesColl.find().toFuture(), 3 seconds)

      res.size shouldBe 7

      val row1=res.filter(_.name.startsWith("Abb"))
      row1.size shouldBe 4
      row1.map(_.name) shouldEqual List("Abbotsfordy", "Abb", "Abbotsford", "Abbotsfordo")
    }

    "can load tsv multiple times to same number" in{

      val param = RunParameters(new File("data/small.tsv"),new File("data/small_codes.tsv"),
        "mongodb://localhost","suggester")
      Main.run(param)

      Thread.sleep(2000)

      val res= Await.result( citiesColl.find().toFuture(), 3 seconds)

      res.size shouldBe 7

      Main.run(param)

      Thread.sleep(2000)


      val res2= Await.result( citiesColl.find().toFuture(), 3 seconds)

      res2.size shouldBe res.size
    }

  }
}
