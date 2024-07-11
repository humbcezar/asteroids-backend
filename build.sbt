name := "asteroids-backend"

version := "0.1"

scalaVersion := "2.13.14"

resolvers += "Akka library repository".at("https://repo.akka.io/maven")
dependencyOverrides += "org.slf4j" % "slf4j-api" % "1.7.36"

val AkkaVersion = "2.9.3"
val AkkaHttpVersion = "10.6.3"
libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor-typed" % AkkaVersion,
  "com.typesafe.akka" %% "akka-stream" % AkkaVersion,
  "com.typesafe.akka" %% "akka-http" % AkkaHttpVersion,
  "com.typesafe.akka" %% "akka-http-spray-json" % AkkaHttpVersion,
  "ch.qos.logback" % "logback-classic" % "1.2.13",
  "com.typesafe.akka" %% "akka-http-testkit" % AkkaHttpVersion % Test,
  "com.typesafe.akka" %% "akka-stream-testkit" % AkkaVersion % Test,
  "org.scalatest" %% "scalatest" % "3.2.17" % Test,
  "org.mockito" %% "mockito-scala" % "1.16.42" % Test
)