val zioVersion = "1.0.3"

libraryDependencies ++= Seq(
  "com.github.jnr" % "jnr-ffi"            % "2.2.1",
  "dev.zio"        %% "zio"               % zioVersion,
  "dev.zio"        %% "zio-json"          % "0.0.1",
  "dev.zio"        %% "zio-test-sbt"      % zioVersion % "test",
  "dev.zio"        %% "zio-test"          % zioVersion % "test",
  "dev.zio"        %% "zio-test-magnolia" % zioVersion % "test"
)

testFrameworks := Seq(new TestFramework("zio.test.sbt.ZTestFramework"))
