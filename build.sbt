enablePlugins(GraalVMNativeImagePlugin)

scalaVersion := "2.13.7"

libraryDependencies ++= Seq(
  "dev.zio" %% "zio" % "1.0.12",
  "io.d11" %% "zhttp" % "1.0.0.0-RC18",
  "org.scalameta" %% "svm-subs" % "20.2.0",
)

Compile / doc / sources := Seq.empty
makePom / publishArtifact := false
Global / packageDoc / publishArtifact := true


graalVMNativeImageOptions ++= Seq(
  "--static",
  "--no-fallback",
  "--install-exit-handlers",
  "--enable-http",
  "--initialize-at-run-time=io.netty.channel.DefaultFileRegion",
  "--initialize-at-run-time=io.netty.channel.epoll.Native",
  "--initialize-at-run-time=io.netty.channel.epoll.Epoll",
  "--initialize-at-run-time=io.netty.channel.epoll.EpollEventLoop",
  "--initialize-at-run-time=io.netty.channel.epoll.EpollEventArray",
  "--initialize-at-run-time=io.netty.channel.kqueue.KQueue",
  "--initialize-at-run-time=io.netty.channel.kqueue.KQueueEventLoop",
  "--initialize-at-run-time=io.netty.channel.kqueue.KQueueEventArray",
  "--initialize-at-run-time=io.netty.channel.kqueue.Native",
  "--initialize-at-run-time=io.netty.channel.unix.Limits",
  "--initialize-at-run-time=io.netty.channel.unix.Errors",
  "--initialize-at-run-time=io.netty.channel.unix.IovArray",
  "--allow-incomplete-classpath",
)


//fork := true

//run / javaOptions += s"-agentlib:native-image-agent=config-output-dir=src/main/resources/META-INF/native-image"