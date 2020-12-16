package com.dealermade.imageflow.entities.libimageflow

import zio.json.jsonField

case class SecurityImage(
  @jsonField("w") width: Int,
  @jsonField("h") height: Int,
  megapixels: Int
)
