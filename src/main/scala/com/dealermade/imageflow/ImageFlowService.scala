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
import scalaz.Scalaz.ToOptionIdOps
import zio.Task

import java.nio.file.Paths

object ImageFlowService {

  def actualBulkResizeImage(
    bulkResizeCommand: BulkResizeCommand
  )(implicit imageFlowContext: Pointer): Task[Unit] = ???

  def bulkResizeImages(bulkResizeCommand: BulkResizeCommand) =
    for {
      imageFlowContext: Pointer <- ImageFlowNative.createContext()
      _ <- actualBulkResizeImage(bulkResizeCommand)(imageFlowContext)
        .ensuring(
          ImageFlowNative.destroyContext(imageFlowContext)
        )
    } yield ()

  //private to this instance
  private implicit lazy val imageFlowNativeLibrary: ImageFlowNative = ImageFlowNative()

  /*
  def hasErrors: Boolean = imageFlowNativeLibrary.imageflow_context_has_error()

  def getError(offset: Int = 1024): String = ImageFlowNative.getError(offset)

  def getErrorHttpCode: Long = imageFlowNativeLibrary.imageflow_context_error_as_http_code()

  def getErrorCodeAsExitCode: Long = imageFlowNativeLibrary.imageflow_context_error_as_exit_code()
   */
  def resizeImage(resizeCommand: ResizeCommand): Task[Unit] =
    for {
      imageFlowContext: Pointer <- ImageFlowNative.createContext()
      _ <- actualResizeImage(resizeCommand)(imageFlowContext)
        .ensuring(
          ImageFlowNative.destroyContext(imageFlowContext)
        )
    } yield ()

  private def actualResizeImage(
    resizeCommand: ResizeCommand
  )(implicit imageFlowContext: Pointer): Task[Unit] =
    ImageFlowProcessJobBuilder(
      outputImagePath = resizeCommand.outputImagePath,
      inputImagePath = resizeCommand.inputImagePath
    ).decode()
      .constrain(
        mode = ConstrainMode.AspectCrop.some,
        width = resizeCommand.outputWidth.some,
        height = resizeCommand.outputHeight.some
      )
      .encode(
        EncodePreset.JPEG(EncodePreset.MozJPEG(quality = 90, progressive = true))
      )
      .buildLoadAndResize()
}
