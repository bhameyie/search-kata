package com.bhameyie.suggester.importer

import com.bhameyie.suggester.database.DatabaseCityRecord

object LineProcessor {
  def process(line: String): List[DatabaseCityRecord] = {
    val tsvSplit = line.split('\t')
    val rec = FileCityRecord(tsvSplit)
    RecordConverter.convert(rec).toList
  }
}
