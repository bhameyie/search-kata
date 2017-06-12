package com.bhameyie.suggester.importer

import com.bhameyie.suggester.database.DatabaseCityRecord

object LineProcessor {
  def process(codeCache:Map[String,String])(line: String): List[DatabaseCityRecord] = {
    val tsvSplit = line.split('\t')
    val rec = FileCityRecord(tsvSplit)
    RecordConverter.convert(codeCache,rec).toList
  }

}


