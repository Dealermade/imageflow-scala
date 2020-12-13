package com.dealermade.imageflow
import jnr.ffi.byref.{LongLongByReference, PointerByReference}
import jnr.ffi.{Memory, Pointer}

object Main extends App {

	lazy val imageFlowNativeLibrary:ImageFlowNative = ImageFlowNative()

	val versionMajor: Long = imageFlowNativeLibrary.imageflow_abi_version_major()
	val versionMinor: Long = imageFlowNativeLibrary.imageflow_abi_version_minor()

	val isCompatible: Boolean = imageFlowNativeLibrary.imageflow_abi_compatible(versionMajor, versionMinor)
	println(s"Imageflow Native: version major $versionMajor & version minor $versionMinor is ${if (isCompatible) "compatible" else "not comptabile"}")

	//  **Contexts are not thread-safe!** Once you create a context, *you* are responsible for ensuring that it is never involved in two overlapping API calls.
	// Should add a lock
	val context: Pointer = imageFlowNativeLibrary.imageflow_context_create(versionMajor, versionMinor)

	def AddInputBytes(context: Pointer, ioId: IoMode, buffer: Array[Byte], offset: Long): Boolean = {
		val pointer = Memory.allocateDirect(context.getRuntime, offset.toInt, true)
		pointer.put(0, buffer, 0, offset.toInt)
		imageFlowNativeLibrary.imageflow_context_add_input_buffer(context, ioId.value, pointer, offset, OutlivesContext.value)
	}

	import better.files._
	val fileStream: Array[Byte] = Resource.getAsStream("jpeg2000-home.jpg").readAllBytes()
	println(fileStream.map(ybte => Integer.toHexString(ybte & 0xFF)).take(5).mkString(" "))
	val isFinished: Boolean = AddInputBytes(context, None, fileStream, fileStream.length)
	println(s"Is adding input finished ? $isFinished")

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

		println(s"Does the image flow context has errors ? ${imageFlowNativeLibrary.imageflow_context_has_error(context)}")

		println(imageFlowNativeLibrary.imageflow_context_error_code(context))
		println(imageFlowNativeLibrary.imageflow_context_error_recoverable(context))
		println(imageFlowNativeLibrary.imageflow_context_error_try_clear(context))
		println(imageFlowNativeLibrary.imageflow_context_error_as_exit_code(context))

		val methodGetInfo: String = "v0.1/get_image_info"
		val newCommands: Array[Byte] = "{\"io_id\":0}".getBytes
		val response: Pointer = imageFlowNativeLibrary.imageflow_context_send_json(context, methodGetInfo, newCommands, newCommands.length)

		val newArray: Array[Byte]= new Array[Byte](1024)
		response.get(0, newArray, 0, 1024)
		println(new String(newArray))

		val commands: Array[Byte] = ("{\"io\":[{\"io_id\":0,\"direction\":\"in\",\"io\":\"placeholder\"},{\"io_id\":1,\"direction\":\"out\",\"io\":\"placeholder\"}],\"framewise\":{\"steps\":[{\"decode\":{\"io_id\":0}},{\"constrain\":{\"mode\":\"within\",\"w\":400}},\"rotate_90\",{\"encode\":{\"io_id\":1,\"preset\":{\"pngquant\":{\"quality\":80}}}}]}}").getBytes
		val method: String = "v1/execute"
		imageFlowNativeLibrary.imageflow_context_send_json(context, method, commands, commands.length)

		val ap = new PointerByReference
		val bytesWritten = new LongLongByReference(0)
		println(s"Buffer before bytesWritten ${bytesWritten.longValue()} fileStream.length ${fileStream.length}")

		imageFlowNativeLibrary.imageflow_context_get_output_buffer_by_id(context, ReadSequential.value, ap, bytesWritten)

		println(s"Buffer bytesWritten ${bytesWritten.longValue()}")
		val ptr: Pointer = ap.getValue

		val dddd: Array[Byte]= new Array[Byte](bytesWritten.intValue())
		ptr.get(0, dddd, 0, bytesWritten.intValue())

		val file: File = file"src"/"main"/"resources"/"test.png"
		file.writeByteArray(dddd)


	}
}
