package com.dealermade.imageflow

import com.dealermade.imageflow.utils.NativeUtils
import org.bytedeco.javacpp.Pointer
import org.bytedeco.javacpp.annotation._

sealed abstract class Direction(val value: Int)

case object In extends Direction(4)
case object Out extends Direction(8)

sealed abstract class IoMode(val value: Int)

case object None extends Direction(0)
case object ReadSequential extends Direction(1)
case object WriteSequential extends Direction(2)
case object ReadSeekable extends Direction(5)
case object WriteSeekable extends Direction(6)
case object ReadWriteSeekable extends Direction(15)

sealed abstract class Lifetime(val value: Int)

case object OutlivesFunctionCall extends Direction(0)
case object OutlivesContext extends Direction(1)

class ImageFlowNative {
	NativeUtils.loadDLL("imageflow.dll")

	/**
	 * Create an image flow context
	 * @return type void* which is IntByReference in Java/Scala
	 */
	@native @ByPtr @Cast(Array("void*")) def imageflow_context_create(): Pointer

	/**
	 * Terminate an imageFlow Context
	 * @param context a pointer to a reference, which is void*
	 * @return a boolean if the context is terminated or not
	 */
	@native def imageflow_context_begin_terminate(@ByPtr context: Pointer): Boolean

}