name := """ToughDoc"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.11.6"

libraryDependencies ++= Seq(
  javaJdbc,
  cache,
  javaWs,
  "com.typesafe.akka" %% "akka-slf4j" % "2.4.0",
  "com.typesafe.akka" %% "akka-actor" % "2.4.0",
  "org.webjars" % "bootstrap" % "3.3.6",
  "org.webjars" % "angularjs" % "1.5.0"
)

// Play provides two styles of routers, one expects its actions to be injected, the
// other, legacy style, accesses its actions statically.
routesGenerator := InjectedRoutesGenerator

//fork in run := true

// Play provides two styles of routers, one expects its actions to be injected, the
// other, legacy style, accesses its actions statically.
routesGenerator := InjectedRoutesGenerator
resolvers += Resolver.url("Typesafe Ivy releases", url("https://repo.typesafe.com/typesafe/ivy-releases"))(Resolver.ivyStylePatterns)

//fork in run := true