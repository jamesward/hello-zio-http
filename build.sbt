enablePlugins(LauncherJarPlugin)

name := "hello-zio-http"

scalaVersion := "3.8.4"

resolvers += Resolver.mavenLocal

libraryDependencies ++= Seq(
  "dev.zio" %% "zio"                 % "2.1.26",
  "dev.zio" %% "zio-config-typesafe" % "4.0.7",
  "dev.zio" %% "zio-config-magnolia" % "4.0.7",
  "dev.zio" %% "zio-http"            % "3.11.2",

  "com.github.ghostdogpr" %% "sage-client-zio" % "0.1.0",

  "dev.zio" %% "zio-direct"          % "1.0.0-RC7",

  "org.slf4j" % "slf4j-simple"       % "2.0.18" % Runtime,

  "com.dimafeng" %% "testcontainers-scala-core" % "0.44.1" % Test,
)

Compile / packageDoc / publishArtifact := false

Compile / doc / sources := Seq.empty

javaOptions += "-Djava.net.preferIPv4Stack=true"
