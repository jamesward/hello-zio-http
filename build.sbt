enablePlugins(LauncherJarPlugin)

name := "hello-zio-http"

scalaVersion := "3.7.4"

libraryDependencies ++= Seq(
  "dev.zio" %% "zio"                 % "2.1.24",
  "dev.zio" %% "zio-config-typesafe" % "4.0.6",
  "dev.zio" %% "zio-config-magnolia" % "4.0.6",
  "dev.zio" %% "zio-http"            % "3.7.4",
  "dev.zio" %% "zio-redis"           % "1.1.12",
  "dev.zio" %% "zio-schema-protobuf" % "1.7.6",
  "dev.zio" %% "zio-direct"          % "1.0.0-RC7",
  "org.slf4j" % "slf4j-simple"       % "2.0.17",

  "dev.zio" %% "zio-redis-embedded"  % "1.1.12" % Test,
)

Compile / packageDoc / publishArtifact := false

Compile / doc / sources := Seq.empty

fork := true

javaOptions += "-Djava.net.preferIPv4Stack=true"

// it'd be cool if we could just run `~Test/reStart` and have it use the right scope
reStart / mainClass := Some("WebAppTest")
reStart / fullClasspath := (Test / fullClasspath).value
//Test / reStart / mainClass := Some("WebAppTest")
//Test / reStart / fullClasspath := (Test / fullClasspath).value
