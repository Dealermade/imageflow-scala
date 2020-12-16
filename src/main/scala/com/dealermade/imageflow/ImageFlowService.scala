package com.dealermade.imageflow

import com.dealermade.imageflow.libimageflow.{
  ConstrainMode,
  EncodePreset,
  ImageBufferLifetime,
  ImageFlowDecodeStep,
  ImageFlowProcessJob,
  ImageFlowProcessJobBuilder,
  IoMode
}
import com.dealermade.imageflow.native.ImageFlowNative
import jnr.ffi.Pointer
import zio.Task

object ImageFlowService {
  //private to this instance
  private implicit lazy val imageFlowNativeLibrary: ImageFlowNative = ImageFlowNative()

  def test(implicit imageFlowContext: Pointer) = {
    val imageJob: ImageFlowProcessJob =
      ImageFlowProcessJobBuilder(
        getClass.getResource("/jpeg2000-home.jpg").getPath,
        IoMode.None,
        ImageBufferLifetime.OutlivesContext
      ).decode(IoMode.None.value)
        // make mode as enum
        .constrain(
          mode = Some(ConstrainMode.AspectCrop),
          width = Some(500),
          height = Some(350)
        )
        .encode(
          IoMode.ReadSequential.value,
          EncodePreset.LossyPNG(EncodePreset.PNGQuant())
        )
        .build

    val result: Either[Boolean, String] = imageJob.addImageToContext()

    result match {
      case Left(_) => println("Image buffer added to the context with success :D")
      case Right(error) =>
        throw new RuntimeException(
          s"An error occurred while adding image buffer to the context $error"
        )
    }

    val imageInfo = imageJob.getImageInfo(IoMode.None.value)
    println(imageInfo)

    val isDone: Boolean = imageJob.addOutputBuffer(IoMode.ReadSequential)
    if (isDone) {
      val imageProcessingResponse = imageJob.processImage()
      println(imageProcessingResponse)
      imageJob.writeOutputImageToFile(
        IoMode.ReadSequential.value,
        "src/test/resources/test.png"
      )
    } else
      throw new RuntimeException(s"An error occurred while writing the image output")

  }
  /*
  def hasErrors: Boolean = imageFlowNativeLibrary.imageflow_context_has_error()

  def getError(offset: Int = 1024): String = ImageFlowNative.getError(offset)

  def getErrorHttpCode: Long = imageFlowNativeLibrary.imageflow_context_error_as_http_code()

  def getErrorCodeAsExitCode: Long = imageFlowNativeLibrary.imageflow_context_error_as_exit_code()
   */
  def resizeImage(resizeCommand: ResizeCommand): Task[Unit] =
    for {
      imageFlowContext: Pointer <- ImageFlowNative.createContext()
      _ <- actualResizeImage(imageFlowContext).ensuring(
        ImageFlowNative.destroyContext(imageFlowContext)
      )
    } yield ()

  private def actualResizeImage(imageFlowContext: Pointer): Task[Unit] =
    for {
      _ <- Task.effect(test(imageFlowContext))
    } yield ()
}
