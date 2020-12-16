package com.dealermade.imageflow

import java.nio.file.Path

final case class BulkResizeCommand(
  inputImagePath: Path,
  outputFiles: List[BulkResizeOutputFile]
)
