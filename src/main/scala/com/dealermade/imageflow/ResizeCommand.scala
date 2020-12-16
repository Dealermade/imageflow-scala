package com.dealermade.imageflow

import com.dealermade.imageflow.libimageflow.ConstrainMode

import java.nio.file.Path

final case class ResizeCommand(
  inputImagePath: Path,
  constrainMode: ConstrainMode,
  outputWidth: Int,
  outputHeight: Int,
  outputImagePath: Path
)
