package com.bhameyie.suggester.importer

sealed trait ValidationFailures

object ValidationFailures {

  case object NoArgumentsProvided extends ValidationFailures
  case object TooManyArgumentsProvided extends ValidationFailures

  case class PathDoesNotExist(path:String) extends ValidationFailures

}
