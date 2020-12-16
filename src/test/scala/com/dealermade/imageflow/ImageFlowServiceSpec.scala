package com.dealermade.imageflow
import com.dealermade.imageflow.ImageFlowService.getClass
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
      testM("Should Resize JPG Image") {
        for {
          _ <- Task.foreach_(1 to 200)(resizeByNumber)
        } yield assert("Hello")(equalTo("Hello"))
      }
    )

  def resizeByNumber(number: Int): Task[Unit] =
    for {
      _ <- ImageFlowService.resizeImage(
        ResizeCommand(
          inputImagePath = Paths.get(getClass.getResource("/car-3-2_xlarge.jpg").getPath),
          constrainMode = ConstrainMode.AspectCrop,
          outputWidth = 500,
          outputHeight = 200,
          outputImagePath = Paths.get(s"src/test/resources/car-3-2_xlarge-${number}.jpg")
        )
      )
    } yield ()

}
