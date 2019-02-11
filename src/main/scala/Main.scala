import java.io.File

import com.typesafe.config._
import services.{CredentialsProvider, S3, Ssm}

object Main extends App {
  def info = Console.err.println(
    """
      |====
      |Migrates a Typesafe (HOCON) config file to AWS Parameter Store.
      |
      |To migrate a file from S3:
      |  sbt "run s3 <profile> <bucket> <path>"
      |
      |Or to migrate a local file:
      |  sbt "run local <profile> <path>"
    """.stripMargin
  )

  args.toList match {
    case "s3" :: profile :: bucket :: path :: Nil => put(fromS3(profile, bucket, path), profile)

    case "local" :: profile :: path :: Nil => put(fromLocal(path), profile)

    case _ => info
  }

  def fromS3(profile: String, bucket: String, path: String): Map[String,String] = {
    val credentialsProvider = CredentialsProvider(profile)

    val raw = S3.getObject(bucket, path, credentialsProvider)
    val config = ConfigFactory.parseString(raw)

    Parameters.fromConfig(config)
  }

  def fromLocal(path: String) = {
    val config = ConfigFactory.parseFile(new File(path))

    Parameters.fromConfig(config)
  }

  def put(parameters: Map[String,String], profile: String) = {
    println(s"Migrating:\n${pretty(parameters)}")

    val ssm = new Ssm(CredentialsProvider(profile))
    parameters foreach (ssm.put _).tupled
  }

  def pretty(parameters: Map[String,String]): String =
    parameters
      .toSeq
      .sortBy(_._1)
      .foldLeft("") { case (acc, (k,v)) => s"$acc\n$k -> $v" }
}
