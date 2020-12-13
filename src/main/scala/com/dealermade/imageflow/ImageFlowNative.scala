package com.dealermade.imageflow

import com.dealermade.imageflow.utils.NativeUtils
import jnr.ffi.annotations.{In, Out, SaveError}
import jnr.ffi.{LibraryLoader, Pointer}
import jnr.ffi.byref.{LongLongByReference, PointerByReference}
import jnr.ffi.types.{int32_t, ssize_t}

sealed abstract class Direction(val value: Int)
case object In extends Direction(4)
case object Out extends Direction(8)

sealed abstract class IoMode(val value: Int)
case object None extends IoMode(0)
case object ReadSequential extends IoMode(1)
case object WriteSequential extends IoMode(2)
case object ReadSeekable extends IoMode(5)
case object WriteSeekable extends IoMode(6)
case object ReadWriteSeekable extends IoMode(15)

sealed abstract class Lifetime(val value: Int)
case object OutlivesFunctionCall extends Lifetime(0)
case object OutlivesContext extends Lifetime(1)

trait ImageFlowNative {
	def imageflow_abi_version_major(): Long
	def imageflow_abi_version_minor(): Long
	def imageflow_abi_compatible(imageflow_abi_ver_major: Long, imageflow_abi_ver_minor: Long): Boolean

	/**
	 * Create an image flow context
	 * @return type void* which is Pointer in Java/Scala
	 */
	@SaveError def imageflow_context_create(): Pointer

	/**
	 * Terminate an imageFlow Context
	 * @param context a pointer to a reference, which is void*
	 * @return a boolean if the context is terminated or not
	 */
	def imageflow_context_begin_terminate(context: Pointer): Boolean

	def imageflow_io_create_for_file(context: Pointer, ioMode: Int, filename: String): Pointer

	def imageflow_context_create(imageflow_abi_ver_major: Long, imageflow_abi_ver_minor: Long): Pointer

	def imageflow_context_has_error(context: Pointer): Boolean

	/**
	 * bool imageflow_context_add_input_buffer(struct imageflow_context *context, int32_t io_id, const uint8_t *buffer, size_t buffer_byte_count, imageflow_lifetime lifetime);
	 */
	def imageflow_context_add_input_buffer(context: Pointer, @In ioMode: Int, buffer: Pointer, count: Long, lifetime: Int): Boolean

	def imageflow_context_add_output_buffer(context: Pointer, @In ioMode: Int): Boolean

	// bool imageflow_context_get_output_buffer_by_id(struct imageflow_context *context, int32_t io_id, const uint8_t **result_buffer, size_t *result_buffer_length);
	def imageflow_context_get_output_buffer_by_id(context: Pointer, @In @int32_t ioMode: Int, @Out resultBuffer: PointerByReference, @Out resultBufferLength: LongLongByReference): Pointer

	def imageflow_context_error_write_to_buffer(context: Pointer, buffer: Array[Byte], @ssize_t bufferLength: Long, @Out bytesWritten: LongLongByReference): Boolean

	def imageflow_context_error_as_http_code(context: Pointer): Long

	def imageflow_context_send_json(context: Pointer, method: String, @In jsonBuffer: Array[Byte], @In @ssize_t jsonBufferSize: Long): Pointer


	def imageflow_context_error_code(context: Pointer): Int
	def imageflow_context_error_as_exit_code(context: Pointer): Int
	def imageflow_context_error_try_clear(context: Pointer): Boolean
	def imageflow_context_error_recoverable(context: Pointer): Boolean
	def imageflow_context_print_and_exit_if_error(context: Pointer): Boolean

}

object ImageFlowNative {
	def apply(): ImageFlowNative = {
		val dllLib: String = "imageflow"
		System.setProperty("jnr.ffi.library.path", NativeUtils.getPathToApp)
		LibraryLoader.create(classOf[ImageFlowNative]).load(dllLib)
	}
}