name := "openTracingExample"

version := "0.1"

scalaVersion := "2.13.0"
val akkaHttpVersion= "10.1.8"
val akkaVersion= "2.6.0"

libraryDependencies ++= Seq(
  "io.opentracing.contrib" % "opentracing-scala-akka" % "0.1.1",
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpVersion % Test,
  "com.typesafe.akka" %% "akka-stream" % akkaVersion,

  //Logging
  "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2",
  "org.slf4j" % "slf4j-api" % "1.7.30",
  "ch.qos.logback" % "logback-classic" % "1.3.0-alpha5",

  "io.jaegertracing" % "jaeger-client" % "1.1.0"
)



