package com.dealermade.imageflow.entities.libimageflow

sealed abstract class ImageFlowDirection(val value: String)
case object ImageFlowIn extends ImageFlowDirection("in")
case object ImageFlowOut extends ImageFlowDirection("out")
