package com.dealermade.imageflow.entities.libimageflow


sealed abstract class IoMode(val value: Int)
case object IoModeNone        extends IoMode(0)
case object ReadSequential    extends IoMode(1)
case object WriteSequential   extends IoMode(2)
case object ReadSeekable      extends IoMode(5)
case object WriteSeekable     extends IoMode(6)
case object ReadWriteSeekable extends IoMode(15)
