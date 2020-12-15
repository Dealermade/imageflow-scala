package com.dealermade.imageflow.entities.libimageflow

import zio.json.{ jsonField, DeriveJsonDecoder, JsonDecoder }

sealed trait ImageFlowResponse {
  def code: Int
  def success: Boolean
  def message: String
}

case class ImageInfoResponse(code: Int, success: Boolean, message: String, data: Option[ImageInfo])
    extends ImageFlowResponse

case class ImageInfo(@jsonField("image_info") imageInfo: ImageInfoPayload)
case class ImageInfoPayload(
    @jsonField("preferred_mime_type") preferredMimeType: String,
    @jsonField("preferred_extension") preferredExtension: String,
    @jsonField("image_width") width: Int,
    @jsonField("image_height") height: Int,
    @jsonField("frame_decodes_into") frameDecodesInto: Option[String]
)

case class VersionInfoResponse(code: Int, success: Boolean, message: String, data: Option[VersionInfo])
    extends ImageFlowResponse
case class VersionInfo(@jsonField("version_info") versionInfo: VersionInfoPayload)
case class VersionInfoPayload(
    @jsonField("long_version_string") longVersionString: String,
    @jsonField("last_git_commit") lastGitCommit: String,
    @jsonField("dirty_working_tree") dirtyWorkingTree: Boolean,
    @jsonField("build_date") buildDate: String,
    @jsonField("git_tag") gitTag: Option[String],
    @jsonField("git_describe_always") gitDescribeAlways: String
)

case class BuildResultResponse(code: Int, success: Boolean, message: String, data: Option[JobResult])
    extends ImageFlowResponse
case class BuildResult(@jsonField("build_result") buildResult: JobResultPayload)

case class JobResultResponse(code: Int, success: Boolean, message: String, data: Option[JobResult])
    extends ImageFlowResponse
case class JobResult(@jsonField("job_result") jobResult: JobResultPayload)
case class JobResultPayload(
    encodes: Option[List[EncodeResult]],
    decodes: Option[List[DecodeResult]],
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
  def width: Int
  def height: Int
  def ioID: Int
  def preferredMimeType: String
  def preferredExtension: String
}

case class DecodeResult(
    @jsonField("w") width: Int,
    @jsonField("h") height: Int,
    @jsonField("io_id") ioID: Int,
    @jsonField("preferred_mime_type") preferredMimeType: String,
    @jsonField("preferred_extension") preferredExtension: String
) extends ProcessingResult

case class EncodeResult(
    @jsonField("w") width: Int,
    @jsonField("h") height: Int,
    @jsonField("io_id") ioID: Int,
    @jsonField("preferred_mime_type") preferredMimeType: String,
    @jsonField("preferred_extension") preferredExtension: String,
    bytes: String
) extends ProcessingResult

object ImageFlowResponse {
  implicit val encodeResultDecoder: JsonDecoder[EncodeResult]               = DeriveJsonDecoder.gen[EncodeResult]
  implicit val decodeResultDecoder: JsonDecoder[DecodeResult]               = DeriveJsonDecoder.gen[DecodeResult]
  implicit val nodePerfDecoder: JsonDecoder[NodePerf]                       = DeriveJsonDecoder.gen[NodePerf]
  implicit val framePerformanceDecoder: JsonDecoder[FramePerformance]       = DeriveJsonDecoder.gen[FramePerformance]
  implicit val buildPerformanceDecoder: JsonDecoder[BuildPerformance]       = DeriveJsonDecoder.gen[BuildPerformance]
  implicit val versionInfoPayloadDecoder: JsonDecoder[VersionInfoPayload]   = DeriveJsonDecoder.gen[VersionInfoPayload]
  implicit val imageInfoPayloadDecoder: JsonDecoder[ImageInfoPayload]       = DeriveJsonDecoder.gen[ImageInfoPayload]
  implicit val jobResultPayloadDecoder: JsonDecoder[JobResultPayload]       = DeriveJsonDecoder.gen[JobResultPayload]
  implicit val imageInfoDecoder: JsonDecoder[ImageInfo]                     = DeriveJsonDecoder.gen[ImageInfo]
  implicit val versionInfoDecoder: JsonDecoder[VersionInfo]                 = DeriveJsonDecoder.gen[VersionInfo]
  implicit val jobResultDecoder: JsonDecoder[JobResult]                     = DeriveJsonDecoder.gen[JobResult]
  implicit val imageInfoResponseDecoder: JsonDecoder[ImageInfoResponse]     = DeriveJsonDecoder.gen[ImageInfoResponse]
  implicit val versionInfoResponseDecoder: JsonDecoder[VersionInfoResponse] = DeriveJsonDecoder.gen[VersionInfoResponse]
  implicit val buildResultResponseDecoder: JsonDecoder[BuildResultResponse] = DeriveJsonDecoder.gen[BuildResultResponse]
  implicit val jobResultResponseDecoder: JsonDecoder[JobResultResponse]     = DeriveJsonDecoder.gen[JobResultResponse]
  implicit val decoder: JsonDecoder[ImageFlowResponse]                      = DeriveJsonDecoder.gen[ImageFlowResponse]

  def parse(jsonResponse: String): Either[String, JobResultResponse] = {
    import zio.json._
    import com.dealermade.imageflow.entities.libimageflow.ImageFlowResponse._

    val last   = jsonResponse.toCharArray.lastIndexOf('}')
    val before = jsonResponse.slice(0, last + 1)
    before.fromJson[JobResultResponse]
  }

}
