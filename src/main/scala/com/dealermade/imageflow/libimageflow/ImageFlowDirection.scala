package com.dealermade.imageflow.libimageflow

sealed abstract class ImageFlowDirection(val value: String)
case object ImageFlowIn extends ImageFlowDirection("in")
case object ImageFlowOut extends ImageFlowDirection("out")
