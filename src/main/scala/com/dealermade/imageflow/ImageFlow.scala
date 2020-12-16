package com.dealermade.imageflow

import com.dealermade.imageflow.native.ImageFlowNative
import jnr.ffi.Pointer
import zio.Task

object ImageFlowService {
  //private to this instance

  /*
  def hasErrors: Boolean = imageFlowNativeLibrary.imageflow_context_has_error()

  def getError(offset: Int = 1024): String = ImageFlowNative.getError(offset)

  def getErrorHttpCode: Long = imageFlowNativeLibrary.imageflow_context_error_as_http_code()

  def getErrorCodeAsExitCode: Long = imageFlowNativeLibrary.imageflow_context_error_as_exit_code()
   */
  /*
ZIO bracket: ImageFlowNative.destroyContext(
  def resizeImages: Task[Unit] =
    for {
      imageFlowNativeLibrary: ImageFlowNative <- ImageFlowNative()
      imageFlowContext: Pointer               <- ImageFlowNative.createContext()
    } yield ()
 */
}
