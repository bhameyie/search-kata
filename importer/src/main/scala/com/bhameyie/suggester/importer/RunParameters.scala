package com.bhameyie.suggester.importer

import java.io.File


case class RunParameters(dataFile: File=new File("."),
                         adminCodeFile: File=new File("."),
                         mongoUrl: String="", mongoDb: String="")


