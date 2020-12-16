package com.dealermade.imageflow.libimageflow

sealed abstract class IoMode(val value: Int)

object IoMode {
  case object None extends IoMode(0)
  case object ReadSequential extends IoMode(1)
  case object WriteSequential extends IoMode(2)
  case object ReadSeekable extends IoMode(5)
  case object WriteSeekable extends IoMode(6)
  case object ReadWriteSeekable extends IoMode(15)
}
