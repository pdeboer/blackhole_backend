name := """PPLibDataAnalyzer"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.6"

libraryDependencies ++= Seq(
  "io.really" %% "jwt-scala" % "1.2.2",
  jdbc,
  anorm,
  cache,
  ws,
  filters,
  "mysql" % "mysql-connector-java" % "5.1.27"
)


fork in run := true