package com.bhameyie.suggester.api

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import com.bhameyie.suggester.api.rest.ApiComposer
import com.typesafe.config.ConfigFactory

object WebServer {

  def main(args: Array[String]) {

    implicit val system = ActorSystem("Suggester")
    implicit val materializer = ActorMaterializer()
    implicit val executionContext = system.dispatcher

    val conf = ConfigFactory.load()
    val hostName = conf.getString("hosting.name")
    val portNumber = conf.getInt("hosting.port")

    val bindingFuture = Http().bindAndHandle(ApiComposer.compose(conf), hostName, portNumber)

    sys.addShutdownHook(
      bindingFuture
        .flatMap(_.unbind())
        .onComplete(_ => system.terminate()))
  }
}
