
val zioVersion = "1.0.3"
val circeVersion = "0.13.0"

libraryDependencies ++= Seq(
  "com.github.jnr" % "jnr-ffi" % "2.2.1",
  "io.circe"                     %% "circe-core"                    % circeVersion,
  "io.circe"                     %% "circe-generic"                 % circeVersion,
  "io.circe"                     %% "circe-parser"                  % circeVersion,
  "io.circe"                     %% "circe-generic-extras"          % circeVersion,
  "dev.zio" %% "zio" % zioVersion,
  "dev.zio" %% "zio-test-sbt" % zioVersion % "test",
  "dev.zio" %% "zio-test" % zioVersion % "test",
  "dev.zio" %% "zio-test-magnolia" % zioVersion % "test"
)

testFrameworks := Seq(new TestFramework("zio.test.sbt.ZTestFramework"))
