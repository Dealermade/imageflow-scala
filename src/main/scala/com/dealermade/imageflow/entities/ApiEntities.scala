package com.dealermade.imageflow.entities

import spray.json._

sealed abstract class Orientation(val value: Char)
case object Horizontal extends Orientation(value = 'h')
case object Vertical   extends Orientation(value = 'v')

/**
  * An Enum class which represent the constrain
  * Check <a href="https://docs.imageflow.io/json/constrain.html">constrain</a> for more information
  * @param value the value of the constrain
  */
sealed abstract class ConstrainMode(val value: String)

/**
  * "distort" the image to exactly the given dimensions.
  * If only one dimension is specified, behaves like `fit`.
  */
case object Distort extends ConstrainMode("distort")

/**
  * Ensure the result fits within the provided dimensions. No upscaling.
  */
case object Within extends ConstrainMode("within")

/**
  * Fit the image within the dimensions, upscaling if needed
  */
case object Fit extends ConstrainMode("fit")

/**
  * Ensure the image is larger than the given dimensions
  */
case object LargerThan extends ConstrainMode("larger_than")

/**
  * Crop to desired aspect ratio if image is larger than requested, then downscale. Ignores smaller images.
  * If only one dimension is specified, behaves like `within`.
  */
case object WithinCrop extends ConstrainMode("within_crop")

/**
  * Crop to desired aspect ratio, then downscale or upscale to fit.
  * If only one dimension is specified, behaves like `fit`.
  */
case object FitCrop extends ConstrainMode("fit_crop")

/**
  * Crop to desired aspect ratio, no upscaling or downscaling. If only one dimension is specified, behaves like Fit.
  */
case object AspectCrop extends ConstrainMode("aspect_crop")

/**
  * Pad to desired aspect ratio if image is larger than requested, then downscale. Ignores smaller images.
  * If only one dimension is specified, behaves like `within`
  */
case object WithinPad extends ConstrainMode("within_pad")

/**
  * Pad to desired aspect ratio, then downscale or upscale to fit
  * If only one dimension is specified, behaves like `fit`.
  */
case object FitPad extends ConstrainMode("fit_pad")

case class ImageFlow(io: Vector[IO], framewise: Framwise, security: Option[Security])
object ImageFlowJsonProtocol extends DefaultJsonProtocol {
  implicit val ioJsonFormat: RootJsonFormat[IO]              = IOJsonProtocol.ioJsonFormat
  implicit val framewiseJsonFormat: RootJsonFormat[Framwise] = FramwiseJsonProtocol.FramwiseJsonFormat
  implicit val securityJsonFormat: RootJsonFormat[Security]  = SecurityJsonProtocol.SecurityJsonFormat

  implicit object ImageFlowJsonFormat extends RootJsonFormat[ImageFlow] {
    def write(c: ImageFlow) = JsObject(
      List(
        Some("io"        -> c.io.toJson),
        Some("framewise" -> c.framewise.toJson),
        if (c.security.isDefined) Some("security" -> c.security.toJson) else Option.empty,
      ).flatten: _*
    )

    override def read(json: JsValue): ImageFlow = ???
//			json.asJsObject.getFields("io", "framewise", "security") match {
//			case Seq(io, framewise, security) => ImageFlow(io.convertTo[Vector[IO]], framewise.convertTo[Framwise], Some(security.convertTo[Security]))
//			case Seq(io, framewise) => ImageFlow(io.convertTo[Vector[IO]], framewise.convertTo[Framwise], Option.empty)
//			case _ => throw DeserializationException("ImageFlow entity is expected!")
//		}
  }
}

/**
  * An entity to be used in Image flow API
  * @param ioID the io mode if for the current context/image
  * @param direction should be in or out
  * @param io should be placeholder // TODO check it out
  */
case class IO(ioID: Int, direction: Option[String], io: Option[String] = Some("placeholder"))
object IOJsonProtocol extends DefaultJsonProtocol {
  implicit val ioJsonFormat: RootJsonFormat[IO] = jsonFormat(IO, "io_id", "direction", "io")
}

case class Security(maxDecodeSize: SecurityImage, maxFrameSize: SecurityImage, maxEncodeSize: SecurityImage)

object SecurityJsonProtocol extends DefaultJsonProtocol {
  implicit val SecurityJsonFormat: RootJsonFormat[Security] = {
    implicit val securityImageJsonProtocol: SecurityImageJsonProtocol.SecurityImageJsonFormat.type =
      SecurityImageJsonProtocol.SecurityImageJsonFormat
    jsonFormat(Security, "max_decode_size", "max_frame_size", "max_encode_size")
  }
}

case class SecurityImage(width: Int, height: Int, megapixels: Int)

object SecurityImageJsonProtocol extends DefaultJsonProtocol {
  implicit object SecurityImageJsonFormat extends RootJsonFormat[SecurityImage] {
    def write(c: SecurityImage) = JsObject(
      "w"          -> JsNumber(c.width),
      "h"          -> JsNumber(c.height),
      "megapixels" -> JsNumber(c.megapixels)
    )

    override def read(json: JsValue): SecurityImage = ???
//		= json.asJsObject.getFields("w", "h", "megapixels") match {
//			case Seq(JsNumber(width), JsNumber(height), JsNumber(megapixels)) => SecurityImage(width.toInt, height.toInt, megapixels.toInt)
//			case _ => throw DeserializationException("SecurityImage entity is expected!")
//		}
  }
}

case class Framwise(steps: Vector[Step])
object FramwiseJsonProtocol extends DefaultJsonProtocol {

  implicit val rotationJsonFormat: RootJsonFormat[Rotation]   = RotationJsonProtocol.RotationJsonFormat
  implicit val flipJsonFormat: RootJsonFormat[Flip]           = FlipJsonProtocol.FlipJsonFormat
  implicit val transposeJsonFormat: RootJsonFormat[Transpose] = TransposeJsonProtocol.TransposeJsonFormat
  implicit val decodeJsonFormat: RootJsonFormat[Decode]       = DecodeJsonProtocol.DecodeJsonFormat
  implicit val encodeJsonFormat: RootJsonFormat[Encode]       = EncodeJsonProtocol.EncodeJsonFormat
  implicit val constrainJsonFormat: RootJsonFormat[Constrain] = ConstrainJsonProtocol.constrainJsonFormat

  implicit object FramwiseJsonFormat extends RootJsonFormat[Framwise] {
    def write(c: Framwise) = JsObject(
      "steps" -> JsArray(c.steps.map {
        case rotation: Rotation   => rotationJsonFormat.write(rotation)
        case flip: Flip           => flipJsonFormat.write(flip)
        case transpose: Transpose => transposeJsonFormat.write(transpose)
        case decode: Decode       => decodeJsonFormat.write(decode)
        case encode: Encode       => encodeJsonFormat.write(encode)
        case constrain: Constrain => constrainJsonFormat.write(constrain)
      })
    )

    override def read(json: JsValue): Framwise = ???
  }
}

trait Step

case class Rotation(rotation: String) extends Step
object RotationJsonProtocol extends DefaultJsonProtocol {
  implicit object RotationJsonFormat extends RootJsonFormat[Rotation] {
    def write(c: Rotation)                     = JsString(c.rotation)
    override def read(json: JsValue): Rotation = Rotation(json.toString)
  }
}

case class Flip(flip: String) extends Step
object FlipJsonProtocol extends DefaultJsonProtocol {
  implicit object FlipJsonFormat extends RootJsonFormat[Flip] {
    def write(c: Flip)                     = JsString(c.flip)
    override def read(json: JsValue): Flip = Flip(json.toString)
  }
}

case class Transpose(transpose: String) extends Step
object TransposeJsonProtocol extends DefaultJsonProtocol {
  implicit object TransposeJsonFormat extends RootJsonFormat[Transpose] {
    def write(c: Transpose)                     = JsString(c.transpose)
    override def read(json: JsValue): Transpose = Transpose(json.toString)
  }
}

case class Decode(ioID: Int, commands: Vector[DecodeStep]) extends Step
object DecodeJsonProtocol extends DefaultJsonProtocol {

  implicit val discardColorProfileJsonFormat: RootJsonFormat[DiscardColorProfile] =
    DiscardColorProfileJsonProtocol.DiscardColorProfileJsonFormat
  implicit val ignoreColorProfileErrorsJsonFormat: RootJsonFormat[IgnoreColorProfileErrors] =
    IgnoreColorProfileErrorsJsonProtocol.IgnoreColorProfileErrorsJsonFormat
  implicit val jpegDownscaleHintsHintJsonFormat: RootJsonFormat[JpegDownscaleHints] =
    JpegDownscaleHintsJsonProtocol.jpegDownscaleHintsHintJsonFormat
  implicit val webpDecoderHintsJsonFormat: RootJsonFormat[WebpDecoderHints] =
    WebpDecoderHintsJsonProtocol.webpDecoderHintsJsonFormat

  implicit object DecodeJsonFormat extends RootJsonFormat[Decode] {
    def write(c: Decode) = JsObject(
      "decode" -> JsObject(
        List(
          Some("io_id" -> JsNumber(c.ioID)),
          if (c.commands.nonEmpty) Some("commands" -> JsArray(c.commands.map {
            case discardColorProfile: DiscardColorProfile => discardColorProfileJsonFormat.write(discardColorProfile)
            case ignoreColorProfileErrors: IgnoreColorProfileErrors =>
              ignoreColorProfileErrorsJsonFormat.write(ignoreColorProfileErrors)
            case jpegDownscaleHints: JpegDownscaleHints => jpegDownscaleHintsHintJsonFormat.write(jpegDownscaleHints)
            case webpDecoderHints: WebpDecoderHints     => webpDecoderHintsJsonFormat.write(webpDecoderHints)
          }))
          else Option.empty,
        ).flatten: _*
      )
    )

    override def read(json: JsValue): Decode = ???
  }
}

trait DecodeStep
case class DiscardColorProfile(value: String = "discard_color_profile") extends DecodeStep
object DiscardColorProfileJsonProtocol extends DefaultJsonProtocol {
  implicit object DiscardColorProfileJsonFormat extends RootJsonFormat[DiscardColorProfile] {
    def write(c: DiscardColorProfile)                     = JsString(c.value)
    override def read(json: JsValue): DiscardColorProfile = DiscardColorProfile(json.toString)
  }
}

case class IgnoreColorProfileErrors(value: String = "ignore_color_profile_errors") extends DecodeStep
object IgnoreColorProfileErrorsJsonProtocol extends DefaultJsonProtocol {
  implicit object IgnoreColorProfileErrorsJsonFormat extends RootJsonFormat[IgnoreColorProfileErrors] {
    def write(c: IgnoreColorProfileErrors)                     = JsString(c.value)
    override def read(json: JsValue): IgnoreColorProfileErrors = IgnoreColorProfileErrors(json.toString)
  }
}

case class JpegDownscaleHints(jpegDownscaleHints: JpegDownscaleHint) extends DecodeStep
object JpegDownscaleHintsJsonProtocol extends DefaultJsonProtocol {
  implicit val jpegDownscaleHintHintJsonFormat: RootJsonFormat[JpegDownscaleHint] =
    JpegDownscaleHintJsonProtocol.jpegDownscaleHintHintJsonFormat
  implicit val jpegDownscaleHintsHintJsonFormat: RootJsonFormat[JpegDownscaleHints] =
    jsonFormat(JpegDownscaleHints, "jpeg_downscale_hints")
}

case class JpegDownscaleHint(width: Int,
                             height: Int,
                             scaleLumaSpatially: Boolean,
                             gammaCorrectForSrgbDuringSpatialLumaScaling: Boolean)
object JpegDownscaleHintJsonProtocol extends DefaultJsonProtocol {
  implicit val jpegDownscaleHintHintJsonFormat: RootJsonFormat[JpegDownscaleHint] = jsonFormat(
    JpegDownscaleHint,
    "width",
    "height",
    "scale_luma_spatially",
    "gamma_correct_for_srgb_during_spatial_luma_scaling"
  )
}

case class WebpDecoderHints(webpDecoderHints: WebpDecoderHint) extends DecodeStep
object WebpDecoderHintsJsonProtocol extends DefaultJsonProtocol {
  implicit val webpDecoderHintJsonFormat: RootJsonFormat[WebpDecoderHint] =
    WebpDecoderHintJsonProtocol.webpDecoderHintJsonFormat
  implicit val webpDecoderHintsJsonFormat: RootJsonFormat[WebpDecoderHints] =
    jsonFormat(WebpDecoderHints, "webp_decoder_hints")
}

case class WebpDecoderHint(width: Int, height: Int)
object WebpDecoderHintJsonProtocol extends DefaultJsonProtocol {
  implicit val webpDecoderHintJsonFormat: RootJsonFormat[WebpDecoderHint] =
    jsonFormat(WebpDecoderHint, "width", "height")
}

case class Encode(ioID: Int, preset: Preset) extends Step
object EncodeJsonProtocol extends DefaultJsonProtocol {
  implicit val jpegJsonProtocol: RootJsonFormat[JPEG]               = JPEGJsonProtocol.jpegJsonFormat
  implicit val gifJsonProtocol: RootJsonFormat[GIF]                 = GIFJsonProtocol.GIFJsonFormat
  implicit val webPJsonProtocol: RootJsonFormat[WebP]               = WebPJsonProtocol.WebPJsonFormat
  implicit val losslessPNGJsonProtocol: RootJsonFormat[LosslessPNG] = LosslessPNGJsonProtocol.losslessPNGJsonFormat
  implicit val lossyPNGJsonProtocol: RootJsonFormat[LossyPNG]       = LossyPNGJsonProtocol.lossyPNGJsonFormat

  implicit object EncodeJsonFormat extends RootJsonFormat[Encode] {
    def write(c: Encode) = JsObject(
      "encode" -> JsObject(
        "io_id" -> JsNumber(c.ioID),
        "preset" -> (c.preset match {
          case jpeg: JPEG               => jpegJsonProtocol.write(jpeg)
          case gif: GIF                 => gifJsonProtocol.write(gif)
          case webP: WebP               => webPJsonProtocol.write(webP)
          case losslessPNG: LosslessPNG => losslessPNGJsonProtocol.write(losslessPNG)
          case lossyPNG: LossyPNG       => lossyPNGJsonProtocol.write(lossyPNG)
        })
      )
    )

    override def read(json: JsValue): Encode = ???
  }
}

case class Constrain(constrain: ConstrainStep) extends Step
object ConstrainJsonProtocol extends DefaultJsonProtocol {
  implicit val constrainStepJsonFormat: RootJsonFormat[ConstrainStep] =
    ConstrainStepJsonProtocol.constrainStepJsonFormat
  implicit val constrainJsonFormat: RootJsonFormat[Constrain] = jsonFormat(Constrain, "constrain")
}

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
case class ConstrainStep(mode: Option[String] = Option.empty,
                         width: Option[Int] = Option.empty,
                         height: Option[Int] = Option.empty,
                         gravity: Option[Gravity] = Option.empty,
                         canvasColor: Option[String] = Option.empty,
                         hints: Option[Hints] = Option.empty)
object ConstrainStepJsonProtocol extends DefaultJsonProtocol {
  implicit val hintsJsonProtocol: RootJsonFormat[Hints]   = HintsJsonProtocol.hintsJsonFormat
  implicit val gravityJsonFormat: RootJsonFormat[Gravity] = GravityJsonProtocol.gravityJsonFormat
  implicit val constrainStepJsonFormat: RootJsonFormat[ConstrainStep] =
    jsonFormat(ConstrainStep, "mode", "w", "h", "gravity", "canvas_color", "hints")
}

case class Hints(sharpenPercent: Int)
object HintsJsonProtocol extends DefaultJsonProtocol {
  implicit val hintsJsonFormat: RootJsonFormat[Hints] = jsonFormat(Hints, "sharpen_percent")
}

case class Gravity(percentage: Coordination)
object GravityJsonProtocol extends DefaultJsonProtocol {
  implicit val coordinationJsonFormat: RootJsonFormat[Coordination] = CoordinationJsonProtocol.coordinationJsonFormat
  implicit val gravityJsonFormat: RootJsonFormat[Gravity]           = jsonFormat(Gravity, "percentage")
}

case class Coordination(x: Int, y: Int)
object CoordinationJsonProtocol extends DefaultJsonProtocol {
  implicit val coordinationJsonFormat: RootJsonFormat[Coordination] = jsonFormat(Coordination, "x", "y")
}

/**
  * For more information, check this link https://docs.imageflow.io/json/encode.html
  */
trait Preset

case class JPEG(mozjpeg: MozJPEG) extends Preset
object JPEGJsonProtocol extends DefaultJsonProtocol {
  implicit val mozJPEGJsonFormat: RootJsonFormat[MozJPEG] = MozJPEGJsonProtocol.mozJPEGJsonFormat
  implicit val jpegJsonFormat: RootJsonFormat[JPEG]       = jsonFormat(JPEG, "mozjpeg")
}

case class MozJPEG(quality: Int, progressive: Boolean)
object MozJPEGJsonProtocol extends DefaultJsonProtocol {
  implicit val mozJPEGJsonFormat: RootJsonFormat[MozJPEG] = jsonFormat(MozJPEG, "quality", "progressive")
}

case class GIF(value: String = "gif") extends Preset
object GIFJsonProtocol extends DefaultJsonProtocol {
  implicit object GIFJsonFormat extends RootJsonFormat[GIF] {
    def write(c: GIF)                     = JsString(c.value)
    override def read(json: JsValue): GIF = GIF(json.toString)
  }
}

case class WebP(value: String = "webplossless") extends Preset
object WebPJsonProtocol extends DefaultJsonProtocol {
  implicit object WebPJsonFormat extends RootJsonFormat[WebP] {
    def write(c: WebP)                     = JsString(c.value)
    override def read(json: JsValue): WebP = WebP(json.toString)
  }
}

case class LosslessPNG(lodePNG: LodePNG) extends Preset
object LosslessPNGJsonProtocol extends DefaultJsonProtocol {
  implicit val lodePNGJsonFormat: RootJsonFormat[LodePNG]         = LodePNGJsonProtocol.lodePNGJsonFormat
  implicit val losslessPNGJsonFormat: RootJsonFormat[LosslessPNG] = jsonFormat(LosslessPNG, "lodepng")
}

case class LodePNG(maximumDeflate: Boolean)
object LodePNGJsonProtocol extends DefaultJsonProtocol {
  implicit val lodePNGJsonFormat: RootJsonFormat[LodePNG] = jsonFormat(LodePNG, "maximum_deflate")
}

case class LossyPNG(pngQuant: PNGQuant) extends Preset
object LossyPNGJsonProtocol extends DefaultJsonProtocol {
  implicit val pngQuantJsonProtocol: RootJsonFormat[PNGQuant] = PNGQuantJsonProtocol.pngQuantJsonFormat
  implicit val lossyPNGJsonFormat: RootJsonFormat[LossyPNG]   = jsonFormat(LossyPNG, "pngquant")
}

case class PNGQuant(quality: Int = 100,
                    speed: Option[Int] = Option.empty,
                    minimumQuality: Option[Int] = Option.empty,
                    maximumDeflate: Option[Boolean] = Option.empty)
object PNGQuantJsonProtocol extends DefaultJsonProtocol {
  implicit val pngQuantJsonFormat: RootJsonFormat[PNGQuant] =
    jsonFormat(PNGQuant, "quality", "speed", "minimum_quality", "maximum_deflate")
}

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
