package com.arquivei.core.configs

import com.arquivei.core.formats.Json._
import org.json4s.JsonAST.JValue
import org.json4s._
import org.json4s.jackson.JsonMethods._
import org.yaml.snakeyaml.Yaml

import scala.util.Try

object OptionsParser {
  /**
   * Sets internal options based on the .yaml file
   *
   * @param file            Yaml config file.
   * @param extraArgs       Extra args for Scio / Beam. Mainly used for Scio Test flags.
   * @param optionOverrides Which of the extraArgs should override Options
   * @return List of arguments for Spotify Scio
   */
  def apply(
             file: String,
             extraArgs: Array[String] = Array(),
             optionOverrides: Seq[String] = Nil
           ): Array[String] = {
    val config = scala.io.Source.fromFile(file).mkString
    val hashmap = new Yaml().load[java.util.Map[String, AnyRef]](config)
    val json = render(hashmap)
    val valuesToOverride = configOverrides(extraArgs, optionOverrides)

    implicit val options: Options = Options.setValue(json merge valuesToOverride)

    (getArgs(json) ++ extraArgs).toArray
  }

  def getArgs(configs: JValue): Seq[String] = {
    implicit val defaultFormats = DefaultFormats
    val fields = (configs \ "kafka").asInstanceOf[JObject].obj
    fields map { case (key, value) =>
      val strValue = value match {
        case JObject(_) => compact(value)
        case _ => value.extract[String]
      }
      s"--$key=$strValue"
    }
  }

  def configOverrides(extraArgs: Array[String], optionOverrides: Seq[String] = Nil): JValue = {
    val regex = "(?s)--([^=]+)=(.+)".r
    val fields = extraArgs
      .flatMap(arg => regex.findFirstMatchIn(arg))
      .map(m => {
        val key = m.group(1)
        val value = m.group(2)
        key -> value
      })
      .filter(config => optionOverrides.contains(config._1))
      .map({
        case (key, value) =>
          val parsedValue = Try(parse(value)).getOrElse(JString(value))
          key -> parsedValue
      })
      .toList

    JObject(
      "options" -> JObject(fields)
    )
  }
}
