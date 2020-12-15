package com.dealermade.imageflow.entities.api
import com.dealermade.imageflow.entities.libimageflow.ConstrainMode
import java.nio.file.Path

final case class ResizeCommand(inputFile: Path, constrainMode: ConstrainMode, outputFile: Path)
