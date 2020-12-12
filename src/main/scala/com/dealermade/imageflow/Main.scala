package com.dealermade.imageflow

import com.dealermade.imageflow.utils.NativeUtils
import jnr.ffi.byref.LongLongByReference
import jnr.ffi.{LibraryLoader, Pointer}

object Main extends App {
	val dllLib: String = "imageflow"
	System.setProperty("jnr.ffi.library.path", NativeUtils.getPathToApp)
	lazy val imageFlowNativeLibrary:ImageFlowNative = LibraryLoader.create(classOf[ImageFlowNative]).load(dllLib)

	val versionMajor: Long = imageFlowNativeLibrary.imageflow_abi_version_major()
	val versionMinor: Long = imageFlowNativeLibrary.imageflow_abi_version_minor()

	val isCompatible: Boolean = imageFlowNativeLibrary.imageflow_abi_compatible(versionMajor, versionMinor)
	println(s"Imageflow Native: version major $versionMajor & version minor $versionMinor is ${if (isCompatible) "compatible" else "not comptabile"}")

	//  **Contexts are not thread-safe!** Once you create a context, *you* are responsible for ensuring that it is never involved in two overlapping API calls.
	// Should add a lock
	val context: Pointer = imageFlowNativeLibrary.imageflow_context_create(versionMajor, versionMinor)
	println(context)

	def AddInputBytes(context: Pointer, ioId: IoMode, buffer: Array[Byte], offset: Long): Boolean = {
		import jnr.ffi.Memory
		val pointer: Pointer = Memory.allocate(context.getRuntime, offset.toInt)
		pointer.put(0, buffer, 0, offset.toInt)
		imageFlowNativeLibrary.imageflow_context_add_input_buffer(context, ioId.value, pointer, offset, OutlivesContext.value)
//		val isFinished: Boolean = imageFlowNativeLibrary.imageflow_context_add_input_buffer(context, ioId.value, pointer, offset, OutlivesContext.value)
//		println(s"$isFinished and byte written ${pointer.arrayLength()}")
//		println(pointer.array().asInstanceOf[Array[Byte]].map(_.toChar).mkString)
//		isFinished
	}

	import better.files._
	val fileStream: Array[Byte] = Resource.getAsStream("jpeg2000-home.jpg").readAllBytes()
	val isFinished: Boolean = AddInputBytes(context, None, fileStream, fileStream.length)
	println(isFinished)

	if (!isFinished) {
		val buffer = new Array[Byte](fileStream.length)
		val bytesWritten = new LongLongByReference()
		imageFlowNativeLibrary.imageflow_context_error_write_to_buffer(context, buffer, fileStream.length, bytesWritten)
		println(buffer.map(_.toChar).mkString)
	} else {
		val httpResult: Long = imageFlowNativeLibrary.imageflow_context_error_as_http_code(context)
		println(httpResult)
		val isDone: Boolean = imageFlowNativeLibrary.imageflow_context_add_output_buffer(context, ReadSequential.value)
		println(isDone)
		val commands: Array[Byte] = ("{\"io\":[{\"io_id\":0,\"direction\":\"in\",\"io\":\"placeholder\"},{\"io_id\":1,\"direction\":\"out\",\"io\":\"placeholder\"}],\"framewise\":{\"steps\":[{\"decode\":{\"io_id\":0}},{\"constrain\":{\"mode\":\"within\",\"w\":400}},\"rotate_90\",{\"encode\":{\"io_id\":1,\"preset\":{\"pngquant\":{\"quality\":80}}}}]}}").getBytes
		val method: String = "v1/execute"
		val error: Pointer = imageFlowNativeLibrary.imageflow_context_send_json(context, method, commands, commands.length)

		val buffer = new Array[Byte](fileStream.length)
		val bytesWritten = new LongLongByReference()
		val result: Pointer = imageFlowNativeLibrary.imageflow_context_get_output_buffer_by_id(context, ReadSequential.value, buffer, bytesWritten)

		println(buffer.map(_.toChar).mkString)
		println(result.array().asInstanceOf[Array[Byte]].map(_.toChar).mkString)
	}
}
