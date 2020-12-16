package com.dealermade.imageflow.libimageflow

sealed abstract class ImageFlowApiPath(val api: String)
case object GetImageInfo extends ImageFlowApiPath("v0.1/get_image_info")
case object Execute extends ImageFlowApiPath("v1/execute")
