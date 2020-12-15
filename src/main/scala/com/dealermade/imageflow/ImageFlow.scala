package com.dealermade.imageflow

import com.dealermade.imageflow.native.ImageFlowNative
import jnr.ffi.Pointer
import zio.Task

class ImageFlow {
  //private to this instance
  private[this] implicit lazy val imageFlowNativeLibrary: ImageFlowNative = ImageFlowNative()
  private[this] implicit lazy val context: Pointer                        = ImageFlowNative.createContext()

  def hasErrors: Boolean = imageFlowNativeLibrary.imageflow_context_has_error(context)

  def getError(offset: Int = 1024): String = ImageFlowNative.getError(offset)

  def getErrorHttpCode: Long = imageFlowNativeLibrary.imageflow_context_error_as_http_code(context)

  def getErrorCodeAsExitCode: Long = imageFlowNativeLibrary.imageflow_context_error_as_exit_code(context)

  def destroyContext: Boolean = ImageFlowNative.destroyContext()

  def resizeImages: Task[Unit] = ???

}
