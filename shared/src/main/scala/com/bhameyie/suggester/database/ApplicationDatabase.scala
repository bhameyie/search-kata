package com.bhameyie.suggester.database


object ApplicationDatabase {

  import java.util

  import com.typesafe.config.Config
  import org.bson.codecs.configuration.CodecRegistry
  import org.mongodb.scala.{MongoClient, MongoDatabase}


  def apply(config: Config): MongoDatabase = {

    import org.bson.codecs.configuration.CodecRegistries.fromRegistries
    import org.mongodb.scala.bson.codecs.DEFAULT_CODEC_REGISTRY

    import scala.collection.JavaConverters._

    val registries: util.List[CodecRegistry] = (List(DEFAULT_CODEC_REGISTRY) ++ DatabaseCityRecord.codecs).asJava

    val codecRegistry = fromRegistries(registries)

    val mongo = MongoClient(config.getString("mongo.url"))

    sys.addShutdownHook(mongo.close())

    mongo.getDatabase(config.getString("mongo.database")).withCodecRegistry(codecRegistry)
  }
}
