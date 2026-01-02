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

lazy val reStartTest =
  inputKey[spray.revolver.AppProcess]("re-start, but test")

reStartTest :=
  Def.inputTask {
    spray.revolver.Actions.restartApp(
      streams.value,
      reLogTag.value,
      thisProjectRef.value,
      reForkOptions.value,
      Some("WebAppTest"),
      (Test / fullClasspath).value,
      reStartArgs.value,
      spray.revolver.Actions.startArgsParser.parsed
    )
  }.dependsOn(Compile / products).evaluated