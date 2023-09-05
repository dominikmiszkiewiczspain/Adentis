ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.11"

libraryDependencies += "com.github.nscala-time" %% "nscala-time" % "2.32.0"

lazy val root = (project in file("."))
  .settings(
    name := "Adentis-exercise"
  )
