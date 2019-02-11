import java.io.File

import com.typesafe.config._
import services.{CredentialsProvider, S3}

object Main extends App {
  def info = Console.err.println(
    """
      |run s3 <profile> <bucket> <path>
      |
      |run local <path>
    """.stripMargin
  )

  if (args.isEmpty) info

  val mode = args(0)

  mode match {
    case "s3" =>
      args.toList match {
        case _ :: profile :: bucket :: path :: Nil => s3(profile, bucket, path)
        case _ => info
      }
    case "local" =>
      args.toList match {
        case path :: Nil => local(path)
      }
    case _ => info
  }

  def s3(profile: String, bucket: String, path: String) = {
    val raw = S3.getObject(bucket, path, CredentialsProvider(profile))
    val config = ConfigFactory.parseString(raw)

    val parameters = Parameters.fromConfig(config)

    println(parameters)
  }

  def local(path: String) = {
    val config = ConfigFactory.parseFile(new File(path))

    val parameters = Parameters.fromConfig(config)

    println(parameters)
  }
}
