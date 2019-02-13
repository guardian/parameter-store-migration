import java.io.File

import com.typesafe.config.ConfigFactory
import org.scalatest.{FlatSpec, Matchers}

class ParametersSpec extends FlatSpec with Matchers {

  val expectedParams = Map(
    "akka.actor.fork-join-executor.parallelism-factor" -> "1",
    "akka.actor.fork-join-executor.parallelism-max" -> "24",
    "akka.boolValue" -> "true",
    "akka.loggers.0" -> "akka.event.Logging$DefaultLogger",
    "akka.loggers.1" -> "akka.event.slf4j.Slf4jLogger",
    "akka.loglevel" -> "WARNING",
    "akka.listOfObjects.0.a" -> "x",
    "akka.listOfObjects.1.a" -> "y"
  )

  it should "produce parameters" in {
    val config = ConfigFactory.parseFile(new File(getClass.getResource("/test.conf").getPath))

    val parameters = Parameters.fromConfig(config)

    parameters should equal(expectedParams)
  }
}
