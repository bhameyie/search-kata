package com.bhameyie.suggester.importer

import java.nio.file.Path

import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{FileIO, Framing}
import akka.util.ByteString

import scala.concurrent.{Await, ExecutionContext}

object AdminCodeCache {

  import scala.concurrent.duration._

  def apply(path: Path)
           (implicit materializer: ActorMaterializer,
            executionContext: ExecutionContext): Map[String, String] = {

    val cache = FileIO.fromPath(path).via(Framing.delimiter(ByteString("\n"), Int.MaxValue))
      .map(_.utf8String)
      .map(_.split('\t'))
      .runFold(Map.empty[String, String])((c, a) =>
        c ++ Map(a.head -> a(1))
      )

    Await.result(cache, 30 seconds)
  }
}
