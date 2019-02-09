import java.io.File

import com.typesafe.config._

object Main extends App {
  if (args.isEmpty) {
    Console.err.println(s"run <filename>")
  }

  val file = args(0)

  val config = ConfigFactory.parseFile(new File(file))

  val parameters = Parameter.fromConfig(config)

  println(parameters)
}
