import com.typesafe.sbt.packager.docker.DockerPermissionStrategy

lazy val zhttp = ProjectRef(uri(s"git://github.com/jamesward/zio-http.git"), "zhttp")

dependsOn(zhttp)

enablePlugins(LauncherJarPlugin, DockerPlugin)

name := "hello-zio-http"

scalaVersion := "2.13.5"

scalacOptions ++= Seq(
  "-unchecked",
  "-deprecation",
  "-explaintypes",
  "-feature",
  "-Wconf:any:error",
  "-Wunused",
  "-Wvalue-discard",
)

Global / sources in (Compile,doc) := Seq.empty
Global / publishArtifact in (Compile, packageDoc) := false

dockerUpdateLatest := true
dockerBaseImage := "gcr.io/distroless/java:11"
daemonUserUid in Docker := None
daemonUser in Docker := "root"
dockerPermissionStrategy := DockerPermissionStrategy.None
dockerEntrypoint := Seq("java", "-jar",s"/opt/docker/lib/${(artifactPath in packageJavaLauncherJar).value.getName}")
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
packageName in Docker := maybeDockerSettings.map(_._3).getOrElse(name.value)