package com.dealermade.imageflow

import com.dealermade.imageflow.native.ImageFlowNative
import jnr.ffi.Pointer
import zio.Task

object ImageFlowService {
  //private to this instance
  private implicit lazy val imageFlowNativeLibrary: ImageFlowNative = ImageFlowNative()

  /*
  def hasErrors: Boolean = imageFlowNativeLibrary.imageflow_context_has_error()

  def getError(offset: Int = 1024): String = ImageFlowNative.getError(offset)

  def getErrorHttpCode: Long = imageFlowNativeLibrary.imageflow_context_error_as_http_code()

  def getErrorCodeAsExitCode: Long = imageFlowNativeLibrary.imageflow_context_error_as_exit_code()
   */
  def resizeImages: Task[Unit] =
    ImageFlowNative.createContext().bracket((d: Pointer) => ImageFlowNative.destroyContext()(imageFlowNativeLibrary)(d)) { implicit imageFlowContext: Pointer => {
      for {
        _ <- Task.fail(new Throwable("Nothing here yet, broken bracket"))
      } yield ()
    } 
}
