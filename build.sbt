name := "parameter-store-migration"

version := "0.1"

scalaVersion := "2.12.8"

val awsSdkVersion = "1.11.495"

libraryDependencies ++= Seq(
  "com.typesafe" % "config" % "1.3.2",
  "com.amazonaws" % "aws-java-sdk-s3" % awsSdkVersion,
  "com.amazonaws" % "aws-java-sdk-ssm" % awsSdkVersion,
  "org.scalatest" %% "scalatest" % "3.0.5" % "test"
)
