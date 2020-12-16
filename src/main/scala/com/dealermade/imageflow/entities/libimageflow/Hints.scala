package com.dealermade.imageflow.entities.libimageflow

import zio.json.jsonField

case class Hints(@jsonField("sharpen_percent") sharpenPercent: Int)
