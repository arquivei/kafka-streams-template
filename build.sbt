val kafkaVersion = "2.4.0"

val dockerRepoUrl = "us.gcr.io/arquiveiprod"

lazy val commonSettings = Defaults.coreDefaultSettings ++ Seq(
  organization := "com.arquivei",
  scalaVersion := "2.12.10"
)

lazy val core = (project in file("./shared/core"))
  .settings(
    commonSettings,
    name := "Core",
    libraryDependencies ++= Seq(
      "org.yaml" % "snakeyaml" % "1.25",
      "org.json4s" %% "json4s-jackson" % "3.6.7",
      "org.slf4j" % "slf4j-jdk14" % "1.7.30",
      "org.scalatest" %% "scalatest" % "3.0.5" % "test"
    )
  )

lazy val example = (project in file("./streams/example"))
  .dependsOn(core)
  .enablePlugins(JavaAppPackaging)
  .settings(
    commonSettings,
    name := "Example",
    packageName in Docker := s"$dockerRepoUrl/example",
    version in Docker := "2002.1",
    libraryDependencies ++= Seq(
      "org.apache.kafka" %% "kafka" % kafkaVersion,
      "org.apache.kafka" %% "kafka-streams-scala" % kafkaVersion,
      "org.apache.kafka" % "kafka-streams-test-utils" % kafkaVersion % "test",
      "org.scalatest" %% "scalatest" % "3.0.5" % "test"
    )
  )
