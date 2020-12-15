package com.dealermade.imageflow.entities

import io.circe.generic.auto._, io.circe.syntax._

case class ImageFlowResponse(code: Int, success: Boolean, message: String, data: Option[AnyRef])

object ImageFlowResponse {

  def parse(jsonResponse: ImageFlowResponseStruct): ImageFlowResponse = {
    val last   = jsonResponse.data.get().toCharArray.lastIndexOf('}')
    val before = jsonResponse.data.get().slice(0, last + 1)
    JobResponseJsonFormat.read(parse(before).as[ImageFlowResponse])
  }

}
