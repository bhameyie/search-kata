package com.bhameyie.suggester.importer

import com.bhameyie.suggester.database.DatabaseCityRecord

/**
  * Handles a string containg a tab seperateb row of city data
  */
object LineProcessor {

  def process(codeCache:Map[String,String])(line: String): List[DatabaseCityRecord] = {
    val tsvSplit = line.split('\t')
    val rec = FileCityRecord(tsvSplit)
    RecordConverter.convert(codeCache,rec).toList
  }

}


