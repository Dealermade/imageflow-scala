package com.dealermade.imageflow.entities

case class JsonResponse(code: Int, success: Boolean, message: String, data: Option[AnyRef])

object JsonResponse {
  import spray.json._
  implicit object JobResponseJsonFormat extends RootJsonFormat[JsonResponse] {
    def write(c: JsonResponse): JsValue = ???

    override def read(json: JsValue): JsonResponse =
      json.asJsObject.getFields("code", "success", "message", "data") match {
        case Seq(JsNumber(code), JsString(success), JsString(message)) =>
          JsonResponse(code.toInt, success.toBoolean, message, Option.empty)
        case Seq(JsNumber(code), JsBoolean(success), JsString(message), JsObject(data)) =>
          JsonResponse(code.toInt, success, message, Some(data))
        case _ => throw DeserializationException("JobResponse entity is expected!")
      }
  }

  def parse(jsonResponse: JsonResponseStruct): JsonResponse = {
    val last   = jsonResponse.data.get().toCharArray.lastIndexOf('}')
    val before = jsonResponse.data.get().slice(0, last + 1)
    JobResponseJsonFormat.read(before.parseJson)
  }

}
