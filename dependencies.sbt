
val zioVersion = "1.0.3"

libraryDependencies ++= Seq(
  "com.github.jnr" % "jnr-ffi" % "2.2.1",
  compilerPlugin(
    ("org.typelevel" %% "kind-projector" % "0.11.2")
      .cross(CrossVersion.full)
  ),
  "dev.zio" %% "zio-test-sbt" % zioVersion % "test",
  "dev.zio" %% "zio-test" % zioVersion % "test",
  "dev.zio" %% "zio-test-magnolia" % zioVersion % "test"
)
