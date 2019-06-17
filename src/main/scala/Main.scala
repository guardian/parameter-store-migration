import java.io.File

import com.typesafe.config._
import services.{CredentialsProvider, S3, Ssm}

object Main extends App {

  Options.parse(args) match {
    case Some(Options(dryRun, overwrite, Some(S3Mode), profile, Some(bucket), path, prefix)) =>
      put(fromS3(profile, bucket, path, prefix), profile, dryRun, overwrite)

    case Some(Options(dryRun, overwrite, Some(LocalMode), profile, _, path, prefix)) =>
      put(fromLocal(path, prefix), profile, dryRun, overwrite)

    case _ => println(Options.usage)
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

  def put(parameters: Map[String,String], profile: String, dryRun: Boolean, overwrite: Boolean) = {
    println(s"\nMigrating (dryRun = $dryRun, overwrite = $overwrite):\n${pretty(parameters)}")

    if (!dryRun) {
      val ssm = new Ssm(CredentialsProvider(profile), overwrite)
      parameters foreach (ssm.put _).tupled
    }
  }

  def pretty(parameters: Map[String,String]): String =
    parameters
      .toSeq
      .sortBy(_._1)
      .foldLeft("") { case (acc, (k,v)) => s"$acc\n$k -> $v" }
}
