import com.typesafe.config.{Config, ConfigValue, ConfigValueType}

import collection.JavaConverters._

trait Parameter
case class StringParameter(key: String, value: String) extends Parameter
case class StringListParameter(key: String, value: List[String]) extends Parameter

object Parameter {

  def fromConfig(config: Config): Set[Parameter] = config.entrySet().asScala.flatMap { entrySet =>
    Parameter.fromLeafNodeConfigValue(entrySet.getKey, entrySet.getValue, config)
  }.toSet

  /**
    * Convert a typesafe ConfigValue to a Parameter Store value.
    *
    * Assumes configValue is either a primitive or a list of strings. This will be true if
    * iterating over an entrySet, which returns all leaf nodes.
    *
    * Note that this means you cannot have a list of objects, as this cannot be serialised to Parameter Store.
    */
  def fromLeafNodeConfigValue(key: String, configValue: ConfigValue, config: Config): Option[Parameter] = {
    println(s"$key: ${configValue.unwrapped()}")
    configValue.valueType match {
      case ConfigValueType.STRING | ConfigValueType.NUMBER | ConfigValueType.BOOLEAN =>
        Some(StringParameter(key, configValue.unwrapped.toString))

      case ConfigValueType.LIST =>
        //Only allow a list of strings
        val listValues: List[String] = config.getList(key).asScala.toList.flatMap { listItemValue: ConfigValue =>
          listItemValue.valueType match {
            case ConfigValueType.STRING | ConfigValueType.NUMBER | ConfigValueType.BOOLEAN =>
              Some(listItemValue.unwrapped.toString)
            case other =>
              println(s"Invalid list item of type $other for key $key. Only lists of primitives can be serialised to Parameter Store.")
              None
          }
        }

        Some(StringListParameter(key, listValues))

      case ConfigValueType.NULL =>
        println(s"Ignoring NULL value for key $key")
        None

      case ConfigValueType.OBJECT =>
        println(s"Unexpected type of object for key $key. Is this a leaf node?")
        None
    }
  }
}
