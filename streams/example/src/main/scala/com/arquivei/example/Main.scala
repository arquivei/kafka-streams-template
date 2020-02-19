package com.arquivei.example

import com.arquivei.core.configs.{Options, OptionsParser}
import org.apache.kafka.streams.{KafkaStreams, Topology}
import org.apache.kafka.streams.scala.ImplicitConversions._
import org.apache.kafka.streams.scala.Serdes._
import org.apache.kafka.streams.scala.StreamsBuilder

object Main {
  def main(cmdArgs: Array[String]): Unit = {
    OptionsParser(cmdArgs(0), cmdArgs.drop(1))

    val config = Options().getKafkaOptions()
    val topology = buildPipeline()
    val streams = new KafkaStreams(topology, config)
    streams.start()
  }

  def buildPipeline(): Topology = {
    val inputTopic = Options().getInputTopics().head
    val outputTopic = Options().getOutputTopics().head

    val builder = new StreamsBuilder()
    new ExamplePipeline().process(
      builder.stream[String, String](inputTopic))
      .to(outputTopic)
    builder.build()
  }
}