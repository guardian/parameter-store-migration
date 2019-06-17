import scopt.OParser

sealed trait Mode
case object S3Mode extends Mode
case object LocalMode extends Mode

case class Options(
  dryRun: Boolean = false,
  overwrite: Boolean = false,
  mode: Option[Mode] = None,
  profile: String = "",
  bucket: Option[String] = None,
  path: String = "",
  prefix: String = ""
)

object Options {

  def parse(args: Array[String]): Option[Options] =
    OParser.parse(optionsParser, args, Options())

  def usage = OParser.usage(optionsParser)

  private val builder = OParser.builder[Options]

  private val optionsParser = {
    import builder._

    OParser.sequence(
      programName("parameter-store-migration"),
      cmd("s3")
        .action((_, o) => o.copy(mode = Some(S3Mode)))
        .text("Migrate a file from S3")
        .children(
          opt[Unit]('d', "dryRun")
            .optional
            .action((_, o) => o.copy(dryRun = true))
            .text("Prints the parameters that will be created but does not write to Parameter Store"),
          opt[Unit]('o', "overwrite")
            .optional
            .action((_, o) => o.copy(overwrite = true))
            .text("Overwrites existing items in Parameter Store"),
          arg[String]("<profile>")
            .required
            .action((p, o) => o.copy(profile = p))
            .text("AWS profile name"),
          arg[String]("<bucket>")
            .required
            .action((b, o) => o.copy(bucket = Some(b)))
            .text("S3 bucket name"),
          arg[String]("<path>")
            .required
            .action((p, o) => o.copy(path = p))
            .text("Path of the file to migrate"),
          arg[String]("<prefix>")
            .required
            .action((p, o) => o.copy(prefix = p))
            .text("A prefix for the Parameter Store key names")
        ),

      cmd("local")
        .action((_, o) => o.copy(mode = Some(LocalMode)))
        .text("Migrate a local file")
        .children(
          opt[Unit]('d', "dryRun")
            .optional
            .action((_, o) => o.copy(dryRun = true))
            .text("Prints the parameters that will be created but does not write to Parameter Store"),
          opt[Unit]('o', "overwrite")
            .optional
            .action((_, o) => o.copy(overwrite = true))
            .text("Overwrites existing items in Parameter Store"),
          arg[String]("<profile>")
            .required
            .action((p, o) => o.copy(profile = p))
            .text("AWS profile name"),
          arg[String]("<path>")
            .required
            .action((p, o) => o.copy(path = p))
            .text("Path of the file to migrate"),
          arg[String]("<prefix>")
            .required
            .action((p, o) => o.copy(prefix = p))
            .text("A prefix for the Parameter Store key names"),
        )
    )
  }
}
