package com.dealermade.imageflow.libimageflow

import zio.json.jsonField

sealed trait ImageFlowDecodeStep

object ImageFlowDecodeStep {
  case class DiscardColorProfile(value: String = "discard_color_profile")
      extends ImageFlowDecodeStep
  case class IgnoreColorProfileErrors(value: String = "ignore_color_profile_errors")
      extends ImageFlowDecodeStep

  case class JpegDownscaleHints(
    @jsonField("jpeg_downscale_hints") jpegDownscaleHints: JpegDownscaleHint
  ) extends ImageFlowDecodeStep
  case class WebpDecoderHints(
    @jsonField("webp_decoder_hints") webpDecoderHints: WebpDecoderHint
  ) extends ImageFlowDecodeStep

  case class JpegDownscaleHint(
    width: Int,
    height: Int,
    @jsonField("scale_luma_spatially") scaleLumaSpatially: Boolean,
    @jsonField(
      "gamma_correct_for_srgb_during_spatial_luma_scaling"
    ) gammaCorrectForSrgbDuringSpatialLumaScaling: Boolean
  )
  case class WebpDecoderHint(width: Int, height: Int)
}
