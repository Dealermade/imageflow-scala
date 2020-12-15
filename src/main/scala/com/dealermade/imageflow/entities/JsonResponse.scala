package com.dealermade.imageflow.entities

case class ImageFlowResponse(code: Int, success: Boolean, message: String, data: Option[AnyRef])

object ImageFlowResponse {

  implicit val decoder: JsonDecoder[ImageFlowResponse] = DeriveJsonDecoder.gen[ImageFlowResponse]

  /*
  It looks like you need to remove the root {} in order for the JSON to parse.
  We might still need to do that for ZIO json
  def parse(jsonResponse: ImageFlowResponseStruct): ImageFlowResponse = {
    val last   = jsonResponse.data.get().toCharArray.lastIndexOf('}')
    val before = jsonResponse.data.get().slice(0, last + 1)
    JobResponseJsonFormat.read(parse(before).as[ImageFlowResponse])
  }*/

}
