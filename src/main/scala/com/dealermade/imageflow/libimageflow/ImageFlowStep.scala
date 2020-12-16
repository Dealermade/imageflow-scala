package com.dealermade.imageflow.libimageflow

import zio.json.jsonField

sealed trait ImageFlowStep

object ImageFlowStep {
  case class Rotation(rotation: String) extends ImageFlowStep
  case class Flip(flip: String) extends ImageFlowStep
  case class Transpose(transpose: String) extends ImageFlowStep
  case class Decode(
    @jsonField("io_id") ioID: Int,
    commands: Option[Vector[ImageFlowDecodeStep]]
  ) extends ImageFlowStep
  case class Encode(@jsonField("io_id") ioID: Int, preset: EncodePreset)
      extends ImageFlowStep
  case class Constrain(constrain: ConstrainStep) extends ImageFlowStep
}
