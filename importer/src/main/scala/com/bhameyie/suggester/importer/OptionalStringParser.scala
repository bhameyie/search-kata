package com.bhameyie.suggester.importer

object OptionalStringParser {
  def parse(str: String): Option[String] = {
    if (str.replaceAll("\\s", "").isEmpty) None
    else Some(str)
  }
}
