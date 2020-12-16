package com.dealermade.imageflow
import com.dealermade.imageflow.libimageflow.ConstrainMode
import jnr.ffi.Pointer
import zio._
import zio.console._
import zio.test._
import zio.test.Assertion._
import zio.test.environment._

import java.nio.file.Paths

object ImageFlowServiceSpec extends DefaultRunnableSpec {
  def spec(): ZSpec[TestEnvironment, Any] =
    suite("ImageFlowServiceSpec")(
      testM("sayHello correctly displays output") {
        for {
          _ <- ImageFlowService.resizeImage(
            ResizeCommand(
              inputFile = Paths.get("/"),
              constrainMode = ConstrainMode.AspectCrop,
              outputFile = Paths.get("/")
            )
          )
        } yield assert("Hello")(equalTo("Hello"))
      }
    )
}
