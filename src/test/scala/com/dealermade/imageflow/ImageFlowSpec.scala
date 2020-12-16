package com.dealermade.imageflow
import com.dealermade.imageflow.entities.api.ResizeCommand
import com.dealermade.imageflow.entities.libimageflow.ConstrainMode
import jnr.ffi.Pointer
import zio._
import zio.console._
import zio.test._
import zio.test.Assertion._
import zio.test.environment._

import java.nio.file.Paths

object ImageFlowSpec extends DefaultRunnableSpec {
  def spec(): Spec[Any with Pointer, TestFailure[Throwable], TestSuccess] =
    suite("ImageFlowSpec")(
      testM("sayHello correctly displays output") {
        for {
          _ <- ImageFlow.resizeImage(
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
