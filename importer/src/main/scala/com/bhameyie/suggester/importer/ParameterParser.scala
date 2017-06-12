package com.bhameyie.suggester.importer

import java.io.File

import scopt.OptionParser

/**
  * Parser for command line arguments
  */
object ParameterParser {
  def apply(): OptionParser[RunParameters] = {
    new scopt.OptionParser[RunParameters]("importer") {
      head("importer", "0.1")

      opt[File]('f', "data-file").required().valueName("<data file>").
        action( (x, c) => c.copy(dataFile = x) ).
        text("data-file is the tsv file containing cities without header")
       .validate{e=>
         if (e.exists()) Right()
         else Left("the specified <data file> does not exist")
       }

      opt[File]('c', "codes-file").required().valueName("<admin codes file>").
        action( (x, c) => c.copy(adminCodeFile = x) ).
        text("codes-file is the tsv file containing the admins codes without header")
        .validate{e=>
          if (e.exists()) Right()
          else Left("the specified <admin codes file> does not exist")
        }

      opt[String]('u', "mongo-url").required().action( (x, c) =>
        c.copy(mongoUrl= x) ).text("mongo-url is the mongodb url (e.g. mongodb://localhost")
        .validate{str=>
          if (str.startsWith("mongodb://")) Right()
          else Left(s"mongo-url $str is invalid")
        }

      opt[String]('d', "mongo-db").required().action( (x, c) =>
        c.copy(mongoDb= x) ).text("mongo-db is the mongodb url (e.g. suggester")

    }

  }
}
