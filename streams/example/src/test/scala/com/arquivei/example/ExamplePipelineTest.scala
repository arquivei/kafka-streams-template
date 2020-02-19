package com.arquivei.example

import com.arquivei.core.configs.{Options, OptionsParser}
import org.apache.kafka.common.serialization.{StringDeserializer, StringSerializer}
import org.apache.kafka.streams.TopologyTestDriver
import org.scalatest.FlatSpec

class ExamplePipelineTest extends FlatSpec {
  val classLoader = this.getClass.getClassLoader
  val bigqueryYamlFile = classLoader.getResource("config.yaml.dist").getPath

  OptionsParser(bigqueryYamlFile)

  "Pipeline" should "work" in {
    val driver = new TopologyTestDriver(
      com.arquivei.example.Main.buildPipeline(),
      Options().getKafkaOptions()
    )

    driver.createInputTopic("inputTopic", new StringSerializer(), new StringSerializer())
      .pipeInput("message content")

    assertResult("prefix:message content") {
      driver.createOutputTopic("outputTopic", new StringDeserializer(), new StringDeserializer())
        .readValue()
    }
  }
}
