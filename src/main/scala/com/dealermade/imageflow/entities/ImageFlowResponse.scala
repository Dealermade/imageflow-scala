package com.dealermade.imageflow.entities

import zio.json.{ jsonField, DeriveJsonDecoder, JsonDecoder }

sealed trait ResponsePayload

case class VersionInfo(@jsonField("version_info") versionInfo: VersionInfoPayload) extends ResponsePayload
case class VersionInfoPayload(
    @jsonField("long_version_string") longVersionString: String,
    @jsonField("last_git_commit") lastGitCommit: String,
    @jsonField("dirty_working_tree") dirtyWorkingTree: Boolean,
    @jsonField("build_date") buildDate: String,
    @jsonField("git_tag") gitTag: Option[String],
    @jsonField("git_describe_always") gitDescribeAlways: String
)

case class ImageInfo(@jsonField("image_info") imageInfo: ImageInfoPayload) extends ResponsePayload
case class ImageInfoPayload(
    @jsonField("preferred_mime_type") preferredMimeType: String,
    @jsonField("preferred_extension") preferredExtension: String,
    @jsonField("image_width") width: Int,
    @jsonField("image_height") height: Int,
    @jsonField("frame_decodes_into") frameDecodesInto: Option[String]
)

//case class BuildResult(@jsonField("build_result") buildResult: JobResultPayload) extends ResponsePayload
case class JobResult(@jsonField("job_result") jobResult: JobResultPayload) extends ResponsePayload
case class JobResultPayload(
    encodes: List[EncodeResult],
    decodes: List[DecodeResult],
    performance: Option[BuildPerformance]
)

case class BuildPerformance(
    frames: List[FramePerformance]
)

case class FramePerformance(
    nodes: List[NodePerf],
    @jsonField("wall_microseconds") wallMicroseconds: Long,
    @jsonField("overhead_microseconds") overheadMicroseconds: Long
)

case class NodePerf(
    name: String,
    @jsonField("wall_microseconds") wallMicroseconds: Long
)

sealed trait ProcessingResult {
  @jsonField("w") def width: Int
  @jsonField("h") def height: Int
  @jsonField("io_id") def ioID: Int
  @jsonField("preferred_mime_type") def preferredMimeType: String
  @jsonField("preferred_extension") def preferredExtension: String
}

case class DecodeResult(width: Int, height: Int, ioID: Int, preferredMimeType: String, preferredExtension: String)
    extends ProcessingResult

case class EncodeResult(
    width: Int,
    height: Int,
    ioID: Int,
    preferredMimeType: String,
    preferredExtension: String,
    bytes: String
) extends ProcessingResult

case class ImageFlowResponse(code: Int, success: Boolean, message: String, data: Option[ResponsePayload])

object ImageFlowResponse {
  implicit val responsePayloadDecoder: JsonDecoder[ResponsePayload] = DeriveJsonDecoder.gen[ResponsePayload]

  implicit val encodeResultDecoder: JsonDecoder[EncodeResult]             = DeriveJsonDecoder.gen[EncodeResult]
  implicit val decodeResultDecoder: JsonDecoder[DecodeResult]             = DeriveJsonDecoder.gen[DecodeResult]
  implicit val buildPerformanceDecoder: JsonDecoder[BuildPerformance]     = DeriveJsonDecoder.gen[BuildPerformance]
  implicit val framePerformanceDecoder: JsonDecoder[FramePerformance]     = DeriveJsonDecoder.gen[FramePerformance]
  implicit val nodePerfDecoder: JsonDecoder[NodePerf]                     = DeriveJsonDecoder.gen[NodePerf]
  implicit val versionInfoPayloadDecoder: JsonDecoder[VersionInfoPayload] = DeriveJsonDecoder.gen[VersionInfoPayload]
  implicit val imageInfoPayloadDecoder: JsonDecoder[ImageInfoPayload]     = DeriveJsonDecoder.gen[ImageInfoPayload]
  implicit val jobResultPayloadDecoder: JsonDecoder[JobResultPayload]     = DeriveJsonDecoder.gen[JobResultPayload]
  implicit val imageInfoDecoder: JsonDecoder[ImageInfo]                   = DeriveJsonDecoder.gen[ImageInfo]
  implicit val decoder: JsonDecoder[ImageFlowResponse]                    = DeriveJsonDecoder.gen[ImageFlowResponse]

  /*
  It looks like you need to remove the root {} in order for the JSON to parse.
  We might still need to do that for ZIO json
  def parse(jsonResponse: ImageFlowResponseStruct): ImageFlowResponse = {
    val last   = jsonResponse.data.get().toCharArray.lastIndexOf('}')
    val before = jsonResponse.data.get().slice(0, last + 1)
    JobResponseJsonFormat.read(parse(before).as[ImageFlowResponse])
  }*/

}
