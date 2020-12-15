package com.dealermade.imageflow.entities.libimageflow

import zio.json.{ jsonField, DeriveJsonEncoder, JsonEncoder }

case class ImageFlow(io: Vector[IO], framewise: Framwise, security: Option[Security])
object ImageFlow {
  implicit val gravityEncoder: JsonEncoder[Gravity]                     = DeriveJsonEncoder.gen[Gravity]
  implicit val coordinationEncoder: JsonEncoder[Coordination]           = DeriveJsonEncoder.gen[Coordination]
  implicit val decodeStepEncoder: JsonEncoder[DecodeStep]               = DeriveJsonEncoder.gen[DecodeStep]
  implicit val hintsEncoder: JsonEncoder[Hints]                         = DeriveJsonEncoder.gen[Hints]
  implicit val jpegDownscaleHintEncoder: JsonEncoder[JpegDownscaleHint] = DeriveJsonEncoder.gen[JpegDownscaleHint]
  implicit val webpDecoderHintEncoder: JsonEncoder[WebpDecoderHint]     = DeriveJsonEncoder.gen[WebpDecoderHint]
  implicit val presetEncoder: JsonEncoder[Preset]                       = DeriveJsonEncoder.gen[Preset]
  implicit val mozJPEGEncoder: JsonEncoder[MozJPEG]                     = DeriveJsonEncoder.gen[MozJPEG]
  implicit val lodePNGEncoder: JsonEncoder[LodePNG]                     = DeriveJsonEncoder.gen[LodePNG]
  implicit val pngQuantEncoder: JsonEncoder[PNGQuant]                   = DeriveJsonEncoder.gen[PNGQuant]
  implicit val constrainStepEncoder: JsonEncoder[ConstrainStep]         = DeriveJsonEncoder.gen[ConstrainStep]
  implicit val stepEncoder: JsonEncoder[Step]                           = DeriveJsonEncoder.gen[Step]
  implicit val framwiseEncoder: JsonEncoder[Framwise]                   = DeriveJsonEncoder.gen[Framwise]
  implicit val securityEncoder: JsonEncoder[Security]                   = DeriveJsonEncoder.gen[Security]
  implicit val securityImageEncoder: JsonEncoder[SecurityImage]         = DeriveJsonEncoder.gen[SecurityImage]
  implicit val ioEncoder: JsonEncoder[IO]                               = DeriveJsonEncoder.gen[IO]
  implicit val encoder: JsonEncoder[ImageFlow]                          = DeriveJsonEncoder.gen[ImageFlow]

}

/**
  * An entity to be used in Image flow API
  * @param ioID the io mode if for the current context/image
  * @param direction should be in or out
  * @param io should be placeholder // TODO check it out
  */
case class IO(@jsonField("io_id") ioID: Int, direction: Option[String], io: Option[String] = Some("placeholder"))

case class Security(
    @jsonField("max_decode_size") maxDecodeSize: SecurityImage,
    @jsonField("max_frame_size") maxFrameSize: SecurityImage,
    @jsonField("max_encode_size") maxEncodeSize: SecurityImage
)
case class SecurityImage(@jsonField("w") width: Int, @jsonField("h") height: Int, megapixels: Int)

case class Framwise(steps: Vector[Step])

sealed trait Step

case class Rotation(rotation: String)                                                  extends Step
case class Flip(flip: String)                                                          extends Step
case class Transpose(transpose: String)                                                extends Step
case class Decode(@jsonField("io_id") ioID: Int, commands: Option[Vector[DecodeStep]]) extends Step

sealed trait DecodeStep
case class DiscardColorProfile(value: String = "discard_color_profile")            extends DecodeStep
case class IgnoreColorProfileErrors(value: String = "ignore_color_profile_errors") extends DecodeStep

case class JpegDownscaleHints(@jsonField("jpeg_downscale_hints") jpegDownscaleHints: JpegDownscaleHint)
    extends DecodeStep
case class JpegDownscaleHint(
    width: Int,
    height: Int,
    @jsonField("scale_luma_spatially") scaleLumaSpatially: Boolean,
    @jsonField("gamma_correct_for_srgb_during_spatial_luma_scaling") gammaCorrectForSrgbDuringSpatialLumaScaling: Boolean
)

case class WebpDecoderHints(@jsonField("webp_decoder_hints") webpDecoderHints: WebpDecoderHint) extends DecodeStep
case class WebpDecoderHint(width: Int, height: Int)

case class Encode(@jsonField("io_id") ioID: Int, preset: Preset) extends Step
case class Constrain(constrain: ConstrainStep)                   extends Step

/**
  *
  * @param mode check https://docs.imageflow.io/json/constrain.html#constraint-modes
  * @param width The width constraint in pixels
  * @param height The height constraint in pixels
  * @param gravity determines how the image is anchored when cropped or padded. {x: 0, y: 0}
  *                represents top-left, {x: 50, y: 50} represents center, {x:100, y:100}
  *                represents bottom-right. Default: center
  * @param canvasColor See Color (https://docs.imageflow.io/json/constrain.html#colors). The color of padding added to the image.
  * @param hints See https://docs.imageflow.io/json/constrain.html#resampling-hints
  * For more information check https://docs.imageflow.io/json/constrain.html
  */
case class ConstrainStep(
    mode: Option[String] = None,
    @jsonField("w") width: Option[Int] = None,
    @jsonField("h") height: Option[Int] = None,
    gravity: Option[Gravity] = None,
    @jsonField("canvas_color") canvasColor: Option[String] = None,
    hints: Option[Hints] = None
)
case class Hints(@jsonField("sharpen_percent") sharpenPercent: Int)
case class Gravity(percentage: Coordination)
case class Coordination(x: Int, y: Int)

/**
  * For more information, check this link https://docs.imageflow.io/json/encode.html
  */
sealed trait Preset

case class JPEG(mozjpeg: MozJPEG) extends Preset
case class MozJPEG(quality: Int, progressive: Boolean)

case class GIF(value: String = "gif")           extends Preset
case class WebP(value: String = "webplossless") extends Preset

case class LosslessPNG(@jsonField("lodepng") lodePNG: LodePNG) extends Preset
case class LodePNG(@jsonField("maximum_deflate") maximumDeflate: Boolean)

case class LossyPNG(@jsonField("pngquant") pngQuant: PNGQuant) extends Preset
case class PNGQuant(
    quality: Int = 100,
    speed: Option[Int] = None,
    @jsonField("minimum_quality") minimumQuality: Option[Int] = None,
    @jsonField("maximum_deflate") maximumDeflate: Option[Boolean] = None
)
// TODO implement this
//  https://docs.imageflow.io/json/region.html
//  https://docs.imageflow.io/json/crop_whitespace.html
//  https://docs.imageflow.io/json/fill_rect.html
//  https://docs.imageflow.io/json/expand_canvas.html
//  https://docs.imageflow.io/json/watermark.html
//  https://docs.imageflow.io/json/command_string.html
//  https://docs.imageflow.io/json/white_balance_srgb.html
//  https://docs.imageflow.io/json/color_filter_srgb.html
//  https://docs.imageflow.io/json/resampling_hints.html
//  https://docs.imageflow.io/json/graph.html -> use this library http://www.scala-graph.org/guides/json.html
