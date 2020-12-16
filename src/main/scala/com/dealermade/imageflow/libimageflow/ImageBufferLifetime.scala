package com.dealermade.imageflow.libimageflow

sealed abstract class ImageBufferLifetime(val value: Int)

object ImageBufferLifetime {
  case object OutlivesFunctionCall extends ImageBufferLifetime(0)
  case object OutlivesContext extends ImageBufferLifetime(1)
}
