package com.dealermade.imageflow.libimageflow

import zio.json.jsonField

case class Hints(@jsonField("sharpen_percent") sharpenPercent: Int)
