import com.typesafe.sbt.packager.docker.DockerPermissionStrategy

enablePlugins(LauncherJarPlugin, DockerPlugin)

name := "hello-zio-http-options-test"

scalaVersion := "2.13.6"

scalacOptions ++= Seq(
  "-unchecked",
  "-deprecation",
  "-explaintypes",
  "-feature",
  "-Wconf:any:error",
  "-Wunused",
  "-Wvalue-discard",
)

libraryDependencies ++= Seq(
  "io.d11" %% "zhttp" % "1.0.0.0-RC16",
)

Compile / doc / sources := Seq.empty
Compile / packageDoc / publishArtifact := false

dockerUpdateLatest := true
dockerBaseImage := "gcr.io/distroless/java:11"
Docker / daemonUserUid := None
Docker / daemonUser := "root"
dockerPermissionStrategy := DockerPermissionStrategy.None
dockerEntrypoint := Seq("java", "-jar",s"/opt/docker/lib/${(packageJavaLauncherJar / artifactPath).value.getName}")
dockerCmd :=  Seq.empty

val maybeDockerSettings = sys.props.get("dockerImageUrl").flatMap { imageUrl =>
  val parts = imageUrl.split("/")
  if (parts.size == 3) {
    Some((parts(0), parts(1), parts(2)))
  }
  else {
    None
  }
}

dockerRepository := maybeDockerSettings.map(_._1)
dockerUsername := maybeDockerSettings.map(_._2)
Docker / packageName := maybeDockerSettings.map(_._3).getOrElse(name.value)
