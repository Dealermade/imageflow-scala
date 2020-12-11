package com.dealermade.imageflow

import jnr.ffi.annotations.{SaveError, TypeDefinition}
import jnr.ffi.Pointer

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

	def imageflow_context_add_input_buffer(context: Pointer, ioMode: Int, buffer: Pointer, count: Long, lifetime: Int): Boolean

	def malloc(size: Long): Pointer
	def free(pointer: Pointer): Unit
}