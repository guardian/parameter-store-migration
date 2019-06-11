import com.typesafe.config.{Config, ConfigValue, ConfigValueType}

import collection.JavaConverters._

object Parameters {

  /**
    * Assumes the value at the given key is a list and produces a set of keys containing the index.
    * E.g.
    *   foo = ["a","b"]
    * becomes
    *   Map(foo.0 -> a, foo.1 -> b)
    *
    * Supports lists of objects, but not nested lists.
    * Does not support heterogeneous lists unless all elements are primitives - and they're probably not a good idea anyway.
    */
  private def fromList(key: String, config: Config): Map[String,String] =
    config
      .getList(key)
      .asScala
      .zipWithIndex
      .foldLeft(Map.empty[String,String]) { case (params: Map[String,String], (listItemValue: ConfigValue, index)) =>

        listItemValue.valueType match {
          case ConfigValueType.STRING | ConfigValueType.NUMBER | ConfigValueType.BOOLEAN =>
            if (listItemValue.unwrapped.toString.isEmpty) {
              println(s"Ignoring empty string value in list for key $key")
              params
            }
            else params + (s"$key.$index" -> listItemValue.unwrapped.toString)

          case ConfigValueType.OBJECT =>
            params ++ fromConfig(
              config.getConfigList(key).get(index), //This line means *all* items in the list must be objects
              Some(s"$key.$index.")
            )

          case ConfigValueType.NULL =>
            println(s"Ignoring NULL value for key $key")
            params
          case ConfigValueType.LIST =>
            println(s"Ignoring nested lists for key $key.")
            params
        }
      }

  /**
    *
    * @param config The Config from which to produce a set of parameters
    * @param pathPrefix A prefix to add to each key
    * @return A set of parameters that can be sent to Parameter Store (i.e. all strings)
    */
  def fromConfig(config: Config, pathPrefix: Option[String] = None): Map[String,String] = {
    config.entrySet().asScala.foldLeft(Map.empty[String,String]) { (params, entry) =>

      val key = pathPrefix
        .map(pre => s"$pre${entry.getKey}")
        .getOrElse(entry.getKey)

      entry.getValue.valueType match {
        case ConfigValueType.STRING | ConfigValueType.NUMBER | ConfigValueType.BOOLEAN =>
          if (entry.getValue.unwrapped.toString.isEmpty) {
            println(s"Ignoring empty string value for key $key")
            params
          }
          else params + (key -> entry.getValue.unwrapped.toString)

        case ConfigValueType.LIST =>
          params ++ fromList(key, config)

        case ConfigValueType.NULL =>
          println(s"Ignoring NULL value for key $key")
          params

        case ConfigValueType.OBJECT =>
          //This should never happen with Config.entrySet()
          println(s"Unexpected type of object for key $key. Leaf nodes should not be objects!")
          params
      }
    }
  }
}
