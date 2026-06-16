enablePlugins(LauncherJarPlugin)

name := "hello-zio-http"

scalaVersion := "3.8.4"

libraryDependencies ++= Seq(
  "dev.zio" %% "zio"                 % "2.1.26",
  "dev.zio" %% "zio-config-typesafe" % "4.0.7",
  "dev.zio" %% "zio-http"            % "3.11.2",
  "org.slf4j" % "slf4j-simple"       % "2.0.18",
)

Compile / packageDoc / publishArtifact := false

Compile / doc / sources := Seq.empty

javaOptions += "-Djava.net.preferIPv4Stack=true"
