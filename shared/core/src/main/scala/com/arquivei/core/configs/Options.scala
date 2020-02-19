package com.arquivei.core.configs

import java.util.Properties

import com.arquivei.core.exceptions.ArquiveiException
import org.json4s.JsonAST.JValue
import org.json4s._
import org.slf4j.LoggerFactory

import scala.util.{Failure, Success, Try}

class Options(value: JValue) {

  def get: JValue = value

  private implicit val defaultFormats = DefaultFormats

  def getKafkaOptions(): Properties = {
    val p = new Properties()

    (value \ "kafka").extract[Map[String, String]].foreach { case(key,value) =>
      p.put(key, value)
    }
    p
  }

  def getInputTopics(): Seq[String] = {
    val extractedOption = value \ "topics" \ "input"

    def extractT(value: JValue) = {
      Try(value.extract[String]) match {
        case Success(value) =>
          value
        case Failure(err) =>
          throw new ArquiveiException(s"Could not get input topics. Value = $extractedOption. Error = ${err.toString}")
      }
    }

    extractedOption match {
      case JArray(arr) => arr.map(extractT)
      case other => Seq(extractT(other))
    }
  }

  def getOutputTopics(): Seq[String] = {
    val extractedOption = value \ "topics" \ "output"

    def extractT(value: JValue) = {
      Try(value.extract[String]) match {
        case Success(value) =>
          value
        case Failure(err) =>
          throw new ArquiveiException(s"Could not get output topics. Value = $extractedOption. Error = ${err.toString}")
      }
    }

    extractedOption match {
      case JArray(arr) => arr.map(extractT)
      case other => Seq(extractT(other))
    }
  }

  def getOption[T](key: String)(implicit mf: scala.reflect.Manifest[T]): T = {
    val extractedOption = value \ "options" \ key

    Try(extractedOption.extract[T]) match {
      case Success(value) =>
        value
      case Failure(err) =>
        throw new ArquiveiException(s"Could not get Option key $key. Value = $extractedOption. Error = ${err.toString}")
    }
  }

  def getOptionList[T](key: String)(implicit mf: scala.reflect.Manifest[T]): Seq[T] = {
    val extractedOption = value \ "options" \ key

    def extractT(value: JValue) = {
      Try(value.extract[T]) match {
        case Success(value) =>
          value
        case Failure(err) =>
          throw new ArquiveiException(s"Could not get Option key $key. Value = $extractedOption. Error = ${err.toString}")
      }
    }

    extractedOption match {
      case JArray(arr) => arr.map(extractT)
      case other => Seq(extractT(other))
    }
  }

  def getOptionString(key: String): String = getOption[String](key)
  def getOptionInt(key: String): Int = getOption[Int](key)
  def getOptionFloat(key: String): Float = getOption[Float](key)
  def getOptionBoolean(key: String): Boolean = getOption[Boolean](key)
}

object Options {
  val logger = LoggerFactory.getLogger(this.getClass)

  private var defaultOptions: Options = _

  def setValue(value: JValue): Options = {
    defaultOptions = new Options(value)
    defaultOptions
  }

  def apply()(implicit options: Options = defaultOptions): Options = options

  def getDefaultOptions: Options = defaultOptions
}

