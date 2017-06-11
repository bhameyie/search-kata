package com.bhameyie.suggester.importer

import com.bhameyie.suggester.importer.ValidationFailures.{NoArgumentsProvided, PathDoesNotExist, TooManyArgumentsProvided}
import com.typesafe.scalalogging.Logger

object ExecutionLogger {

  def logValidationFailures(logger: Logger)(validationFailures: ValidationFailures): Unit = {

    def helpMsg(msg: String) = logger.error(s"$msg. Please supply a valid path as a command line argument.")

    validationFailures match {
      case TooManyArgumentsProvided =>
        helpMsg("Too many arguments were provided.")

      case NoArgumentsProvided =>
        helpMsg("No path was provided.")

      case PathDoesNotExist(invalidPath) =>
        helpMsg(s"Supplied path '$invalidPath' could not be found.")
    }
  }
}
