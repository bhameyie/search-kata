package com.bhameyie.suggester.database


object ApplicationDatabase {

  import java.util

  import com.typesafe.config.Config
  import org.bson.codecs.configuration.CodecRegistry
  import org.mongodb.scala.{MongoClient, MongoDatabase}

  import org.bson.codecs.configuration.CodecRegistries.fromRegistries
  import org.mongodb.scala.bson.codecs.DEFAULT_CODEC_REGISTRY

  import scala.collection.JavaConverters._


  def apply(config: Config): MongoDatabase = {
    apply(config.getString("mongo.url"), config.getString("mongo.database"))
  }

  def apply(mongoConn: String, database: String): MongoDatabase = {

    val registries: util.List[CodecRegistry] = (List(DEFAULT_CODEC_REGISTRY) ++ DatabaseCityRecord.codecs).asJava

    val codecRegistry = fromRegistries(registries)

    val mongo = MongoClient(mongoConn)

    sys.addShutdownHook(mongo.close())

    mongo.getDatabase(database).withCodecRegistry(codecRegistry)
  }
}
