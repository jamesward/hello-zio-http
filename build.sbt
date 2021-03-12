lazy val zhttp = ProjectRef(uri(s"git://github.com/jamesward/zio-http.git"), "zhttp")

dependsOn(zhttp)

enablePlugins(GraalVMNativeImagePlugin)

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

libraryDependencies ++= Seq(
  "org.scalameta" %% "svm-subs"     % "20.2.0",
)

Global / sources in (Compile,doc) := Seq.empty
Global / publishArtifact in (Compile, packageDoc) := false

graalVMNativeImageOptions ++= Seq(
  "--verbose",
  "--no-server",
  "--allow-incomplete-classpath",
  "--no-fallback",
  "--static",
  "--install-exit-handlers",
  "--libc=musl",
  "-H:+ReportExceptionStackTraces",
  "-H:+RemoveSaturatedTypeFlows",
  "--initialize-at-run-time=io.netty.channel.epoll.Epoll",
  "--initialize-at-run-time=io.netty.channel.epoll.Native",
  "--initialize-at-run-time=io.netty.channel.epoll.EpollEventLoop",
  "--initialize-at-run-time=io.netty.channel.epoll.EpollEventArray",
  "--initialize-at-run-time=io.netty.channel.DefaultFileRegion",
  "--initialize-at-run-time=io.netty.channel.kqueue.KQueueEventArray",
  "--initialize-at-run-time=io.netty.channel.kqueue.KQueueEventLoop",
  "--initialize-at-run-time=io.netty.channel.kqueue.Native",
  "--initialize-at-run-time=io.netty.channel.unix.Errors",
  "--initialize-at-run-time=io.netty.channel.unix.IovArray",
  "--initialize-at-run-time=io.netty.channel.unix.Limits",
  "--initialize-at-run-time=io.netty.util.internal.logging.Log4JLogger",
)