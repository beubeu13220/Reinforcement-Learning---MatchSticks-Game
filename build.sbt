name := "reinforcementLearning"

version := "0.1"

scalaVersion := "2.12.8"

libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.5" % "test"

assemblyJarName in assembly := s"${name.value}-${version.value}.jar"