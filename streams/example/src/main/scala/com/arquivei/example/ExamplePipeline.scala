package com.arquivei.example

import com.arquivei.core.configs.Options
import org.apache.kafka.streams.scala.kstream.KStream

class ExamplePipeline {
  def process(input: KStream[String,String]): KStream[String,String] = {
    val prefix = Options().getOptionString("prefix")
    input.mapValues { value =>
      s"$prefix:$value"
    }
  }
}
