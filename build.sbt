enablePlugins(LauncherJarPlugin)

name := "hello-zio-http"

scalaVersion := "3.7.4"

libraryDependencies ++= Seq(
  "dev.zio" %% "zio"                 % "2.1.24",
  "dev.zio" %% "zio-config-typesafe" % "4.0.6",
  "dev.zio" %% "zio-http"            % "3.7.4",
  "org.slf4j" % "slf4j-simple"       % "2.0.17",
)

Compile / packageDoc / publishArtifact := false

Compile / doc / sources := Seq.empty

fork := true

javaOptions += "-Djava.net.preferIPv4Stack=true"
