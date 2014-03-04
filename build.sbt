// Set the project name to the string 'My Project'
name := "Forum Analysis"

version := "1.0"

// Add multiple dependencies
libraryDependencies ++= Seq(
  "com.typesafe.slick" %% "slick" % "1.0.1",
  "org.specs2" %% "specs2" % "2.3.3" % "test",
  "com.typesafe" %% "scalalogging-slf4j" % "1.0.1",
  "mysql" % "mysql-connector-java" % "5.1.24",
  "org.json4s" %% "json4s-jackson" % "3.2.5",
  "com.typesafe" % "config" % "1.2.0"
)

