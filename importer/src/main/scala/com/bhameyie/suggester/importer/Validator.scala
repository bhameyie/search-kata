package com.bhameyie.suggester.importer

object Validator {

  import java.nio.file.{Files, Path, Paths}

  import cats.data.Validated
  import cats.data.Validated.{Invalid, Valid}

  def validate(args: Array[String]): Validated[ValidationFailures, Path] = {
    args match {
      case Array() => Invalid(ValidationFailures.NoArgumentsProvided)

      case Array(_, _*) => Invalid(ValidationFailures.TooManyArgumentsProvided)

      case Array(single) if OptionalStringParser.parse(single).isEmpty =>
        Invalid(ValidationFailures.NoArgumentsProvided)

      case Array(single) if !Files.exists(Paths.get(single)) =>
        Invalid(ValidationFailures.PathDoesNotExist(single))

      case Array(single) => Valid(Paths.get(single))

    }
  }
}
