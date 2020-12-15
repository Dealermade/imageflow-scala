name := "imageflow-scala"
organization := "com.dealermade"
version := "0.0.0.2"

scalaVersion := "2.13.4"

scalacOptions --= Seq(
  "-Xfatal-warnings",
  "-Ymacro-annotations"
)

scalacOptions ++= Seq(
  "-deprecation"
)

resolvers ++= Seq(
  Resolver.sonatypeRepo("releases"),
  Resolver.sonatypeRepo("snapshots"),
  Resolver.bintrayRepo("jarlakxen", "maven")
)
