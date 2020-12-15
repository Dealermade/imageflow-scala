package com.dealermade.imageflow.entities.api

import java.nio.file.Path

final case class ResizeCommand(inputFile: Path, constrainMode: ConstrainMode, outputFile: Path)
