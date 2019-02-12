### Parameter Store migration

A tool for migrating Typesafe (HOCON) config files to AWS Parameter Store.

To migrate a file from S3:
  `sbt "run s3 <profile> <bucket> <path> <prefix>"`

Or to migrate a local file:
  `sbt "run local <profile> <path> <prefix>"`

E.g. `run s3 my-aws-account my-config-bucket path/to/file.conf /AppName/Stage/`

#### Notes

##### Lists
Lists are represented in Parameter Store by adding the index to the path, e.g.

`akka.loggers = ["akka.event.Logging$DefaultLogger", "akka.event.slf4j.Slf4jLogger"]`

becomes:
```
"akka.loggers.0" -> "akka.event.Logging$DefaultLogger"
"akka.loggers.1" -> "akka.event.slf4j.Slf4jLogger"
```
This works fine with Typesafe config, e.g. `config.getStringList("akka.loggers")`.

It does however mean this tool will not use Parameter Store's `StringList` type.

Unsupported list types:
- Lists containing a mix of primitives and objects.
- Nested lists.