package com.dealermade.imageflow.libimageflow

import zio.json.jsonField

case class Security(
  @jsonField("max_decode_size") maxDecodeSize: SecurityImage,
  @jsonField("max_frame_size") maxFrameSize: SecurityImage,
  @jsonField("max_encode_size") maxEncodeSize: SecurityImage
)
