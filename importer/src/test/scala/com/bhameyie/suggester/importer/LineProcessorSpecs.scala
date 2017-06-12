package com.bhameyie.suggester.importer

import org.scalatest.{Matchers, WordSpec}
import org.scalatest.prop.Checkers

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
