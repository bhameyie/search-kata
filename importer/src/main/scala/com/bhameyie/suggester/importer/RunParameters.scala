package com.bhameyie.suggester.importer

import java.io.File


/**
  * The required parameters needed to perform a data import
  * @param dataFile the primary tsv file
  * @param adminCodeFile the file containing the admin1 codes
  * @param mongoUrl the mondogb uri
  * @param mongoDb the mongodb database name
  */
case class RunParameters(dataFile: File=new File("."),
                         adminCodeFile: File=new File("."),
                         mongoUrl: String="", mongoDb: String="")


