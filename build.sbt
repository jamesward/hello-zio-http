import com.typesafe.sbt.packager.docker.DockerPermissionStrategy

enablePlugins(LauncherJarPlugin, DockerPlugin)

scalaVersion := "2.13.7"

libraryDependencies ++= Seq(
  "dev.zio" %% "zio"         % "1.0.12",
  "io.d11" %% "zhttp"        % "1.0.0.0-RC18",
)

Compile / doc / sources := Seq.empty
Global / packageDoc / publishArtifact := true


dockerUpdateLatest := true
dockerBaseImage := "gcr.io/distroless/java:11"
Docker / daemonUserUid := None
Docker / daemonUser := "root"
dockerPermissionStrategy := DockerPermissionStrategy.None
dockerEntrypoint := Seq("java", "-jar", s"/opt/docker/lib/${(packageJavaLauncherJar / artifactPath).value.getName}")
dockerCmd := Seq.empty

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
