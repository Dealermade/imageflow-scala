import sbt._
import Keys._
import com.dealermade.imageflow.Dependencies._

lazy val root = project
	.in(file("."))
	.settings(
		name := "imageflow-scala",
		version := "0.0.1",
		// Due to the sbt limitation, add fork := true to build.sbt refer to https://github.com/bytedeco/sbt-javacpp/#usage
		fork := true,
		Seq(
			organization := "com.dealermade",
			scalaVersion := Scala.version
		),
		mainClass in (Compile, run) := Some("com.dealermade.imageflow.Main"),
		scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature", "-Xlint", "-Xlog-free-terms"),
		libraryDependencies ++= dependencies(scalaJHttp.value, sprayJson.value, betterFiles.value, jnrFFI.value) ++ testDependencies(scalaTest.value),
	)


