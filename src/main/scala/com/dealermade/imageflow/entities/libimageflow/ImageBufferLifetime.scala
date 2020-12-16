package com.dealermade.imageflow.entities.libimageflow

sealed abstract class ImageBufferLifetime(val value: Int)
case object OutlivesFunctionCall extends ImageBufferLifetime(0)
case object OutlivesContext      extends ImageBufferLifetime(1)
