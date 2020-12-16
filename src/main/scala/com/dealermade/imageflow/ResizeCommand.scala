package com.dealermade.imageflow

import com.dealermade.imageflow.libimageflow.ConstrainMode

import java.nio.file.Path

final case class ResizeCommand(
  inputFile: Path,
  constrainMode: ConstrainMode,
  outputFile: Path
)
