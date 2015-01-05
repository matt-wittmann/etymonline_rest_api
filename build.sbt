organization := "com.mattwittmann"

name := "etymonline_rest_api"

version := "1.0.0-SNAPSHOT"

scalaVersion := "2.11.4"

scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature")

resolvers += "spray repo" at "http://repo.spray.io"

val akkaVersion = "2.3.8"

val sprayVersion = "1.3.2"

val log4j2Version = "2.1"

val jacksonVersion = "2.5.0"

libraryDependencies ++= Seq("org.scala-lang.modules" %% "scala-xml" % "1.0.3",
                            "org.scala-lang" % "scala-reflect" % "2.11.4",
                            "com.typesafe.akka" %% "akka-actor" % akkaVersion,
                            "com.typesafe.akka" %% "akka-testkit" % akkaVersion,
                            "io.spray" %% "spray-client" % sprayVersion,
                            "io.spray" %% "spray-can" % sprayVersion,
                            "io.spray" %% "spray-routing" % sprayVersion,
                            "io.spray" %% "spray-json" % "1.3.1",
                            "nu.validator.htmlparser" % "htmlparser" % "1.4",
                            "org.slf4j" % "slf4j-api" % "1.7.9",
                            "org.apache.logging.log4j" % "log4j-slf4j-impl" % log4j2Version,
                            "org.apache.logging.log4j" % "log4j-api" % log4j2Version,
                            "org.apache.logging.log4j" % "log4j-core" % log4j2Version,
                            "com.fasterxml.jackson.core" % "jackson-core" % jacksonVersion,
                            "com.fasterxml.jackson.core" % "jackson-databind" % jacksonVersion,
                            "com.fasterxml.jackson.core" % "jackson-annotations" % jacksonVersion,
                            "org.scalatest" %% "scalatest" % "2.2.3" % Test,
                            "com.github.tomakehurst" % "wiremock" % "1.53" % Test)