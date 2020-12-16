package com.dealermade.imageflow.libimageflow

import zio.json.internal.Write
import zio.json.{ jsonField, DeriveJsonEncoder, JsonEncoder }
import com.dealermade.imageflow.libimageflow.EncodePreset._
import com.dealermade.imageflow.libimageflow.ImageFlowDecodeStep._
import com.dealermade.imageflow.libimageflow.ImageFlowStep._

case class ImageFlow(io: Vector[IO], framewise: Framwise, security: Option[Security])
object ImageFlow {

  implicit val gravityEncoder: JsonEncoder[Gravity] = DeriveJsonEncoder.gen[Gravity]
  implicit val coordinationEncoder: JsonEncoder[Coordination] =
    DeriveJsonEncoder.gen[Coordination]
  implicit val hintsEncoder: JsonEncoder[Hints] = DeriveJsonEncoder.gen[Hints]
  implicit val jpegDownscaleHintEncoder: JsonEncoder[JpegDownscaleHint] =
    DeriveJsonEncoder.gen[JpegDownscaleHint]
  implicit val webpDecoderHintEncoder: JsonEncoder[WebpDecoderHint] =
    DeriveJsonEncoder.gen[WebpDecoderHint]
  implicit val mozJPEGEncoder: JsonEncoder[MozJPEG] = DeriveJsonEncoder.gen[MozJPEG]
  implicit val lodePNGEncoder: JsonEncoder[LodePNG] = DeriveJsonEncoder.gen[LodePNG]
  implicit val pngQuantEncoder: JsonEncoder[PNGQuant] = DeriveJsonEncoder.gen[PNGQuant]
  implicit val constrainStepEncoder: JsonEncoder[ConstrainStep] =
    DeriveJsonEncoder.gen[ConstrainStep]
  implicit val jpegEncoder: JsonEncoder[JPEG] = DeriveJsonEncoder.gen[JPEG]
  implicit val losslessPNGEncoder: JsonEncoder[LosslessPNG] =
    DeriveJsonEncoder.gen[LosslessPNG]
  implicit val LossyPNGEncoder: JsonEncoder[LossyPNG] = DeriveJsonEncoder.gen[LossyPNG]

  implicit val jpegDownscaleHintsEncoder: JsonEncoder[JpegDownscaleHints] =
    DeriveJsonEncoder.gen[JpegDownscaleHints]

  implicit val decodeStepEncoder: JsonEncoder[ImageFlowDecodeStep] =
    (decodeStep: ImageFlowDecodeStep, indent: Option[Int], out: Write) =>
      decodeStep match {
        case DiscardColorProfile(discardColorProfile) =>
          JsonEncoder[String].unsafeEncode(discardColorProfile, indent, out)
        case IgnoreColorProfileErrors(ignoreColorProfileErrors) =>
          JsonEncoder[String].unsafeEncode(ignoreColorProfileErrors, indent, out)
        case jpegDownscaleHints: JpegDownscaleHints =>
          jpegDownscaleHintsEncoder.unsafeEncode(jpegDownscaleHints, indent, out)
      }

  implicit val presetEncoder: JsonEncoder[EncodePreset] =
    (preset: EncodePreset, indent: Option[Int], out: Write) =>
      preset match {
        case jpeg: JPEG => jpegEncoder.unsafeEncode(jpeg, indent, out)
        case GIF(gif)   => JsonEncoder[String].unsafeEncode(gif, indent, out)
        case WebP(webp) => JsonEncoder[String].unsafeEncode(webp, indent, out)
        case losslessPng: LosslessPNG =>
          losslessPNGEncoder.unsafeEncode(losslessPng, indent, out)
        case lossyPNG: LossyPNG => LossyPNGEncoder.unsafeEncode(lossyPNG, indent, out)
      }

  implicit val decodeEncoder: JsonEncoder[Decode] = DeriveJsonEncoder.gen[Decode]
  implicit val encodeEncoder: JsonEncoder[Encode] = DeriveJsonEncoder.gen[Encode]
  implicit val constrainEncoder: JsonEncoder[Constrain] = DeriveJsonEncoder.gen[Constrain]

  implicit val stepEncoder: JsonEncoder[ImageFlowStep] =
    (step: ImageFlowStep, indent: Option[Int], out: Write) =>
      step match {
        case Rotation(rotation) => JsonEncoder[String].unsafeEncode(rotation, indent, out)
        case Flip(flip)         => JsonEncoder[String].unsafeEncode(flip, indent, out)
        case Transpose(transpose) =>
          JsonEncoder[String].unsafeEncode(transpose, indent, out)
        case decode: Decode =>
          out.write("""{"decode":"""); decodeEncoder.unsafeEncode(decode, indent, out);
          out.write("}")
        case encode: Encode =>
          out.write("""{"encode":"""); encodeEncoder.unsafeEncode(encode, indent, out);
          out.write("}")
        case constrain: Constrain => constrainEncoder.unsafeEncode(constrain, indent, out)
      }

  implicit val framwiseEncoder: JsonEncoder[Framwise] = DeriveJsonEncoder.gen[Framwise]
  implicit val securityEncoder: JsonEncoder[Security] = DeriveJsonEncoder.gen[Security]
  implicit val securityImageEncoder: JsonEncoder[SecurityImage] =
    DeriveJsonEncoder.gen[SecurityImage]
  implicit val ioEncoder: JsonEncoder[IO] = DeriveJsonEncoder.gen[IO]
  implicit val encoder: JsonEncoder[ImageFlow] = DeriveJsonEncoder.gen[ImageFlow]

}

/**
 * For more information, check this link https://docs.imageflow.io/json/encode.html
 */

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
