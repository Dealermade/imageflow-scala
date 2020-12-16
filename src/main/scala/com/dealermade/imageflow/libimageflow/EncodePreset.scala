package com.dealermade.imageflow.libimageflow

import zio.json.jsonField

sealed trait EncodePreset

object EncodePreset {
  case class JPEG(mozjpeg: MozJPEG) extends EncodePreset
  case class MozJPEG(quality: Int, progressive: Boolean)

  case class GIF(value: String = "gif") extends EncodePreset
  case class WebP(value: String = "webplossless") extends EncodePreset

  case class LosslessPNG(@jsonField("lodepng") lodePNG: LodePNG) extends EncodePreset
  case class LodePNG(@jsonField("maximum_deflate") maximumDeflate: Boolean)

  case class LossyPNG(@jsonField("pngquant") pngQuant: PNGQuant) extends EncodePreset
  case class PNGQuant(
    quality: Int = 100,
    speed: Option[Int] = None,
    @jsonField("minimum_quality") minimumQuality: Option[Int] = None,
    @jsonField("maximum_deflate") maximumDeflate: Option[Boolean] = None
  )
}
