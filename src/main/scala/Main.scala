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
      |  sbt "run s3 <profile> <bucket> <path> <prefix>"
      |
      |Or to migrate a local file:
      |  sbt "run local <profile> <path> <prefix>"
      |
      |E.g. `run s3 my-aws-account my-config-bucket path/to/file.conf /AppName/Stage/`
    """.stripMargin
  )

  args.toList match {
    case "s3" :: profile :: bucket :: path :: prefix :: Nil => put(fromS3(profile, bucket, path, prefix), profile)

    case "local" :: profile :: path :: prefix :: Nil => put(fromLocal(path, prefix), profile)

    case _ => info
  }

  def fromS3(profile: String, bucket: String, path: String, prefix: String): Map[String,String] = {
    val credentialsProvider = CredentialsProvider(profile)

    val raw = S3.getObject(bucket, path, credentialsProvider)
    val config = ConfigFactory.parseString(raw)

    Parameters.fromConfig(config, Some(prefix))
  }

  def fromLocal(path: String, prefix: String) = {
    val config = ConfigFactory.parseFile(new File(path))

    Parameters.fromConfig(config, Some(prefix))
  }

  def put(parameters: Map[String,String], profile: String) = {
    println(s"\nMigrating:\n${pretty(parameters)}")

    val ssm = new Ssm(CredentialsProvider(profile))
    parameters foreach (ssm.put _).tupled
  }

  def pretty(parameters: Map[String,String]): String =
    parameters
      .toSeq
      .sortBy(_._1)
      .foldLeft("") { case (acc, (k,v)) => s"$acc\n$k -> $v" }
}
