enablePlugins(JavaAppPackaging)

name := "hello-zio-http"

scalaVersion := "3.10.0"

libraryDependencies ++= Seq(
  "dev.zio" %% "zio"                 % "2.1.26",
  "dev.zio" %% "zio-config-typesafe" % "4.1.0",
  "dev.zio" %% "zio-http"            % "3.11.6",
  "org.slf4j" % "slf4j-simple"       % "2.0.20",
)

Compile / packageDoc / publishArtifact := false

Compile / doc / sources := Seq.empty

javaOptions += "-Djava.net.preferIPv4Stack=true"

// sbt-mcp (loopback-only: its tools can execute build tasks)
ThisBuild / mcpEnabled := true
ThisBuild / mcpHost := "127.0.0.1"
ThisBuild / mcpPort := 5103

// SkillsJars: extract agent Skills with `./sbt extractSkillsJars`
skillsJarsOutputDir := Some(file(".kiro/skills"))

libraryDependencies += "com.jamesward" % "skills" % "0.0.12" % Skills
