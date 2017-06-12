package com.bhameyie.suggester.importer

/**
  * Parser for empty string
  */
object OptionalStringParser {
  /**
    * Parse a string and auto convert it to an option if it is empty or whitespace
    * @param str
    * @return
    */
  def parse(str: String): Option[String] = {
    if (str.replaceAll("\\s", "").isEmpty) None
    else Some(str)
  }
}
