package com.dealermade.imageflow

import com.dealermade.imageflow.utils.NativeUtils
import jnr.ffi.{LibraryLoader, Pointer}

object Main extends App {
	val dllLib: String = "imageflow"
	System.setProperty("jnr.ffi.library.path", NativeUtils.getPathToApp)
	lazy val imageFlowNativeLibrary:ImageFlowNative = LibraryLoader.create(classOf[ImageFlowNative]).load(dllLib)

	val versionMajor: Long = imageFlowNativeLibrary.imageflow_abi_version_major()
	val versionMinor: Long = imageFlowNativeLibrary.imageflow_abi_version_minor()

	val isCompatible: Boolean = imageFlowNativeLibrary.imageflow_abi_compatible(versionMajor, versionMinor)
	println(s"Imageflow Native: version major $versionMajor & version minor $versionMinor is ${if (isCompatible) "compatible" else "not comptabile"}")

	val context: Pointer = imageFlowNativeLibrary.imageflow_context_create(versionMajor, versionMinor)
	println(context)

	def AddInputBytes(context: Pointer, ioId: IoMode, buffer: Array[Byte], offset: Long): Boolean = {
		import jnr.ffi.Memory
		val pointer: Pointer = Memory.allocate(context.getRuntime, offset.toInt)
		imageFlowNativeLibrary.imageflow_context_add_input_buffer(context, ioId.value, pointer, offset, OutlivesFunctionCall.value)
	}

	import better.files._
	val fileStream: Array[Byte] = Resource.getAsStream("jpeg2000-home.jpg").readAllBytes()
	val isFinished: Boolean = AddInputBytes(context, None, fileStream, fileStream.length)
	println(isFinished)
}
