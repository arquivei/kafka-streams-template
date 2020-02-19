package com.arquivei.core.configs

import java.util.Properties

import com.arquivei.core.exceptions.ArquiveiException
import org.scalatest.FlatSpec

class OptionsParserTest extends FlatSpec {
  val classLoader = this.getClass.getClassLoader
  val configFile = classLoader.getResource("config.yaml.dist").getPath

  OptionsParser(configFile, Array("--keyToBeOverwritten=rightValue"), Array("keyToBeOverwritten"))

  "Options" should "return kafka properties" in {
    val p = new Properties()
    p.put("bootstrap.servers", "localhost:9092")
    p.put("application.id", "com.organization.name")
    p.put("security.protocol", "SASL_PLAINTEXT")
    p.put("compression.type", "gzip")
    p.put("sasl.mechanism", "PLAIN")
    p.put("sasl.jaas.config", "org.apache.kafka.common.security.plain.PlainLoginModule required username=\"user\" password=\"pass\";")
    p.put("auto.offset.reset", "earliest")
    assertResult(p)(Options().getKafkaOptions())
  }

  it should "return topic config" in {
    assertResult(Seq("inputTopic"))(Options().getInputTopics())
    assertResult(Seq("outputTopic"))(Options().getOutputTopics())
  }

  it should "return user options" in {
    assertResult("value1")(Options().getOptionString("key1"))
    assertResult("rightValue")(Options().getOptionString("keyToBeOverwritten"))
  }

  it should "throw error if key is missing or wrong type" in {
    assertThrows[ArquiveiException](Options().getOptionString("missingOption"))
  }
}
