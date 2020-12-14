package com.dealermade.imageflow.native
import com.dealermade.imageflow.entities.JsonResponseStruct
import com.dealermade.imageflow.utils.NativeUtils
import jnr.ffi.annotations.{ In, Out, SaveError }
import jnr.ffi.{ LibraryLoader, Pointer }
import jnr.ffi.byref.{ LongLongByReference, PointerByReference }
import jnr.ffi.types.{ int32_t, ssize_t }

sealed abstract class Direction(val value: String)
case object In  extends Direction("in")
case object Out extends Direction("out")

sealed abstract class IoMode(val value: Int)
case object None              extends IoMode(0)
case object ReadSequential    extends IoMode(1)
case object WriteSequential   extends IoMode(2)
case object ReadSeekable      extends IoMode(5)
case object WriteSeekable     extends IoMode(6)
case object ReadWriteSeekable extends IoMode(15)

sealed abstract class Lifetime(val value: Int)
case object OutlivesFunctionCall extends Lifetime(0)
case object OutlivesContext      extends Lifetime(1)

sealed abstract class ImageFlowApi(val api: String)
case object GetImageInfo extends ImageFlowApi("v0.1/get_image_info")
case object Execute      extends ImageFlowApi("v1/execute")

case class ImageFlowVersion(versionMajor: Long, versionMinor: Long)

trait ImageFlowNative {

  /**
	 * Get the image flow version major
	 * @return the version major of the image flow
	 *
	 * C/C++ signature: uint32_t imageflow_abi_version_major(void);
	 */
  def imageflow_abi_version_major(): Long

  /**
	 * Get the image flow version minor
	 * @return the version minor of the image flow
	 *
	 * C/C++ signature: uint32_t imageflow_abi_version_minor(void);
	 */
  def imageflow_abi_version_minor(): Long

  /**
	 * Check if the image flow is compatible to given versions.
	 * Call this method before doing anything else to ensure that your header or
	 * FFI bindings are compatible with the libimageflow that is currently loaded.
	 *
	 * @param imageflow_abi_ver_major the major version of image flow to be used
	 * @param imageflow_abi_ver_minor the minor version of image flow to be used
	 * @return true if the it's compatible, false otherwise
	 *
	 * C/C++ signature: bool imageflow_abi_compatible(uint32_t imageflow_abi_ver_major, uint32_t imageflow_abi_ver_minor);
	 */
  def imageflow_abi_compatible(imageflow_abi_ver_major: Long, imageflow_abi_ver_minor: Long): Boolean

  /**
	 * Creates and returns an imageflow context.
	 * An imageflow context is required for all other imageflow API calls.
	 *
	 * An imageflow context tracks
	 * -  error state
	 * -  error messages
	 * -  stack traces for errors (in C land, at least)
	 * -  context-managed memory allocations
	 * -  performance profiling information
	 *
	 * Warning!! **Contexts are not thread-safe!** Once you create a context, *you* are responsible for ensuring that it is never involved in two overlapping API calls.
	 *
	 * @return A null pointer if allocation fails or the provided interface version is incompatible
	 *
	 * C/C++ signature: struct imageflow_context *imageflow_context_create(uint32_t imageflow_abi_ver_major, uint32_t imageflow_abi_ver_minor);
	 */
  @SaveError def imageflow_context_create(): Pointer

  /**
	 * Begins the process of destroying the context, yet leaves error
	 * information intact so that any errors in the tear-down process
	 * can be debugged with `imageflow_context_error_write_to_buffer`.
	 *
	 * @param context a pointer to a reference, which is void*
	 * @return true if no errors occurred. Returns false if there were tear-down issues.
	 *
	 * C/C++ signature: bool imageflow_context_begin_terminate(struct imageflow_context *context);
	 */
  def imageflow_context_begin_terminate(context: Pointer): Boolean

  /**
	 * Creates and returns an imageflow context.
	 * An imageflow context is required for all other imageflow API calls.
	 *
	 * An imageflow context tracks
	 * - error state
	 * - error messages
	 * - stack traces for errors (in C land, at least)
	 * - context-managed memory allocations
	 * - performance profiling information
	 * Warning!! **Contexts are not thread-safe!** Once you create a context, *you* are responsible for ensuring that it is never involved in two overlapping API calls.
	 *
	 * @param imageflow_abi_ver_major the major version of image flow to be used
	 * @param imageflow_abi_ver_minor the minor version of image flow to be used
	 * @return a null pointer if allocation fails or the provided interface version is incompatible
	 *
	 * C/C++ signature: struct imageflow_context *imageflow_context_create(uint32_t imageflow_abi_ver_major,
	 *         uint32_t imageflow_abi_ver_minor);
	 */
  def imageflow_context_create(imageflow_abi_ver_major: Long, imageflow_abi_ver_minor: Long): Pointer

  /**
	 * Checks if the context has errors
	 *
	 * @param context the current context of image flow
	 * @return true if the context is in an error state. You must immediately deal with the error,
	 *         as subsequent API calls will fail or cause undefined behavior until the error state is cleared
	 *
	 * C/C++ signature: bool imageflow_context_has_error(struct imageflow_context *context);
	 */
  def imageflow_context_has_error(context: Pointer): Boolean

  /**
	 * Adds an input buffer to the job context.
	 * You are ALWAYS responsible for freeing the memory provided (at the time specified by imageflow_lifetime).
	 * If you specify `OutlivesFunctionCall`, then the buffer will be copied.
	 *
	 * @param context the current context of image flow
	 * @param ioMode the io ID
	 * @param buffer the buffer to be added to the context
	 * @param count the buffer length
	 * @param lifetime the way to copy the buffer either OutlivesFunctionCall or OutlivesContext
	 * @return
	 *
	 * C/C++ signature: bool imageflow_context_add_input_buffer(struct imageflow_context *context, int32_t io_id, const uint8_t *buffer, size_t buffer_byte_count, imageflow_lifetime lifetime);
	 */
  def imageflow_context_add_input_buffer(context: Pointer,
                                         @In ioMode: Int,
                                         buffer: Pointer,
                                         count: Long,
                                         lifetime: Int): Boolean

  /**
	 * Adds an output buffer to the job context.
	 * The  buffer will be freed with the context.
	 *
	 * @param context the current context of image flow
	 * @param ioMode the io ID
	 * @return null if allocation failed; check the context for error details.
	 *
	 * C/C++ signature: bool imageflow_context_add_output_buffer(struct imageflow_context *context, int32_t io_id);
	 */
  def imageflow_context_add_output_buffer(context: Pointer, @In ioMode: Int): Boolean

  /**
	 * Provides access to the underlying buffer for the given io id
	 *
	 * @param context the current context of image flow
	 * @param ioMode the io ID
	 * @param resultBuffer the buffer that should be written in it
	 * @param resultBufferLength the length of bytes written in the buffer
	 * @return true if the buffer was written, false otherwise
	 *
	 * C/C++ signature: bool imageflow_context_get_output_buffer_by_id(struct imageflow_context *context,
	 *         int32_t io_id,
	 *         const uint8_t **result_buffer,
	 *         size_t *result_buffer_length);
	 */
  def imageflow_context_get_output_buffer_by_id(context: Pointer,
                                                @In @int32_t ioMode: Int,
                                                @Out resultBuffer: PointerByReference,
                                                @Out resultBufferLength: LongLongByReference): Pointer

  /**
	 * Prints the error messages (and optional stack frames) to the given buffer in UTF-8 form; writes a null
	 * character to terminate the string, and *ALSO* provides the number of bytes written (excluding the null terminator)
	 *
	 * If the data is truncated, "\n[truncated]\n" is written to the buffer
	 *
	 * Please be accurate with the buffer length, or a buffer overflow will occur.
	 *
	 * @param context the current context of image flow
	 * @param buffer the buffer that should be written in it
	 * @param bufferLength The buffer length
	 * @param bytesWritten the length of bytes written in the buffer
	 * @return false if the buffer was too small (or null) and the output was truncated.
	 *         true if all data was written OR if there was a bug in error serialization (that gets written, too).
	 *
	 * C/C++ signature: bool imageflow_context_error_write_to_buffer(struct imageflow_context *context,
	 *         char *buffer,
	 *         size_t buffer_length,
	 *         size_t *bytes_written);
	 */
  def imageflow_context_error_write_to_buffer(context: Pointer,
                                              buffer: Array[Byte],
                                              @ssize_t bufferLength: Long,
                                              @Out bytesWritten: LongLongByReference): Boolean

  /**
	 * Converts the error (or lack thereof) into an equivalent http status code
	 * Values:
	 * - 200 - No error
	 * - 400 - Bad argument/node parameters/graph/json/image/image type
	 * - 401 - Authorization to imageflow server required
	 * - 402 - License error
	 * - 403 - Action forbidden under imageflow security policy
	 * - 404 - Primary resource/file not found
	 * - 500 - Secondary resource/file not found, IO error, no solution error, unknown error, custom error, internal error
	 * - 502 - Upstream server error
	 * - 503 - Out Of Memory condition (malloc/calloc/realloc failed).
	 * - 504 - Upstream timeout
	 *
	 * @param context the current context of image flow
	 * @return the http code
	 *
	 * C/C++ signature: int32_t imageflow_context_error_as_http_code(struct imageflow_context *context);
	 */
  def imageflow_context_error_as_http_code(context: Pointer): Long

  /**
	 * Sends a JSON message to the `imageflow_context` using endpoint `method`.
	 * Endpoints
	 * - 'v1/build'
	 * For endpoints supported by the latest nightly build, see
	 * `https://s3-us-west-1.amazonaws.com/imageflow-nightlies/master/doc/context_json_api.txt`
	 * Notes
	 * - `method` and `json_buffer` are only borrowed for the duration of the function call. You are
	 *   responsible for their cleanup (if necessary - static strings are handy for things like
	 *   `method`).
	 * -  `method` should be a UTF-8 null-terminated string.
	 *    `json_buffer` should be a UTF-8 encoded buffer (not null terminated) of length `json_buffer_size`.
	 *
	 * You should call `imageflow_context_has_error()` to see if this succeeded.
	 * Call `imageflow_json_response_destroy` when you're done with it (or dispose the context).
	 *
	 * @param context the current context of image flow
	 * @param method the endpoint to be called
	 * @param jsonBuffer the buffer to be written
	 * @param jsonBufferSize the buffer size
	 * @return A `struct imageflow_json_response` is returned for success and most error conditions.
	 *
	 * C/C++ signature: const struct imageflow_json_response *imageflow_context_send_json(struct imageflow_context *context,
	 *         const char *method,
	 *         const uint8_t *json_buffer,
	 *         size_t json_buffer_size);
	 */
  def imageflow_context_send_json(context: Pointer,
                                  method: String,
                                  @In jsonBuffer: Array[Byte],
                                  @In @ssize_t jsonBufferSize: Long): JsonResponseStruct

  /**
	 * These will be stabilized after 1.0, once error categories have passed rigorous real-world testing
	 * `imageflow_context_error_as_exit_code` and `imageflow_context_error_as_http_status` are suggested in the meantime.
	 *
	 * @param context the current context of image flow
	 * @return the numeric code associated with the error category. 0 means no error.
	 *
	 * C/C++ signature int32_t imageflow_context_error_code(struct imageflow_context *context);
	 */
  def imageflow_context_error_code(context: Pointer): Int

  /**
	 * Converts the error (or lack thereof) into an unix process exit code
	 * Values
	 *
	 * - 0 - No error
	 * - 64 - Invalid usage (graph invalid, node argument invalid, action not supported)
	 * - 65 - Invalid Json, Image malformed, Image type not supported
	 * - 66 - Primary or secondary file or resource not found.
	 * - 69 - Upstream server errored or timed out
	 * - 70 - Possible bug: internal error, custom error, unknown error, or no graph solution found
	 * - 71 - Out Of Memory condition (malloc/calloc/realloc failed).
	 * - 74 - I/O Error
	 * - 77 - Action forbidden under imageflow security policy
	 * - 402 - License error
	 * - 401 - Imageflow server authorization required
	 *
	 * @param context the current context of image flow
	 * @return an error code, check above for the values
	 *
	 * C/C++ signature int32_t imageflow_context_error_as_exit_code(struct imageflow_context *context);
	 */
  def imageflow_context_error_as_exit_code(context: Pointer): Int

  /**
	 * You must immediately deal with the error, as subsequent API calls will
	 * fail or cause undefined behavior until the error state is cleared
	 *
	 * @param context the current context of image flow
	 * @return true if the context is "ok" or in an error state that is recoverable.
	 *
	 * C/C++ signature bool imageflow_context_error_try_clear(struct imageflow_context *context);
	 */
  def imageflow_context_error_try_clear(context: Pointer): Boolean

  /**
	 * You must immediately deal with the error, as subsequent API calls will
	 * fail or cause undefined behavior until the error state is cleared
	 *
	 * @param context the current context of image flow
	 * @return true if the context is "ok" or in an error state that is recoverable
	 *
	 * C/C++ signature bool imageflow_context_error_recoverable(struct imageflow_context *context);
	 */
  def imageflow_context_error_recoverable(context: Pointer): Boolean

}

object ImageFlowNative {

  /**
	 * Create an image flow native instance that loads the library
	 *
	 * @return a new image flow native library instance
	 */
  def apply(): ImageFlowNative = {
    val dllLib: String = "imageflow"
    System.setProperty("jnr.ffi.library.path", NativeUtils.getPathToApp)
    LibraryLoader.create(classOf[ImageFlowNative]).load(dllLib)
  }

  /**
	 * Get the version of the actual image flow from the native
	 *
	 * @param imageFlowNativeLibrary you should provide implicitly the library
	 * @return an instance of ImageFlowVersion which contains the major and minor version
	 */
  def getVersion()(implicit imageFlowNativeLibrary: ImageFlowNative): ImageFlowVersion = {
    val versionMajor: Long = imageFlowNativeLibrary.imageflow_abi_version_major()
    val versionMinor: Long = imageFlowNativeLibrary.imageflow_abi_version_minor()
    ImageFlowVersion(versionMajor, versionMinor)
  }

  /**
	 * Check if for the given version is compatible or not
	 *
	 * @param versionMajor the major version
	 * @param versionMinor the minor version
	 * @param imageFlowNativeLibrary you should provide implicitly the library
	 * @return true if it's compatible, false otherwise
	 */
  def isCompatible(versionMajor: Long, versionMinor: Long)(implicit imageFlowNativeLibrary: ImageFlowNative): Boolean =
    imageFlowNativeLibrary.imageflow_abi_compatible(versionMajor, versionMinor)

  /**
	 * Check if for the given version is compatible or not
	 *
	 * @param imageFlowVersion an instance of ImageFlowVersion which contains the version
	 * @param imageFlowNativeLibrary you should provide implicitly the library
	 * @return true if it's compatible, false otherwise
	 */
  def isCompatible(imageFlowVersion: ImageFlowVersion)(implicit imageFlowNativeLibrary: ImageFlowNative): Boolean =
    imageFlowNativeLibrary.imageflow_abi_compatible(imageFlowVersion.versionMajor, imageFlowVersion.versionMinor)

  /**
	 * Create an image flow context with a given version
	 * You should handle the errors if the version is not compatible,
	 * use createContext() if you are not sure about the version
	 *
	 * @param versionMajor the major version
	 * @param versionMinor the minor version
	 * @param imageFlowNativeLibrary you should provide implicitly the library
	 * @return the created image flow context
	 */
  def createContext(versionMajor: Long, versionMinor: Long)(implicit imageFlowNativeLibrary: ImageFlowNative): Pointer =
    imageFlowNativeLibrary.imageflow_context_create(versionMajor, versionMinor)

  /**
	 * Create an image flow context with a given version
	 * You should handle the errors if the version is not compatible,
	 * use createContext() if you are not sure about the version
	 *
	 * @param imageFlowVersion an instance of ImageFlowVersion which contains the version
	 * @param imageFlowNativeLibrary you should provide implicitly the library
	 * @return the created image flow context
	 */
  def createContext(imageFlowVersion: ImageFlowVersion)(implicit imageFlowNativeLibrary: ImageFlowNative): Pointer =
    imageFlowNativeLibrary.imageflow_context_create(imageFlowVersion.versionMajor, imageFlowVersion.versionMinor)

  /**
	 * Create an image flow context
	 *
	 * @param imageFlowNativeLibrary you should provide implicitly the library
	 * @return the created image flow context
	 */
  def createContext()(implicit imageFlowNativeLibrary: ImageFlowNative): Pointer = {
    val version: ImageFlowVersion = ImageFlowNative.getVersion()
    imageFlowNativeLibrary.imageflow_context_create(version.versionMajor, version.versionMinor)
  }

  def getErrorBytes(offset: Int = 1024)(
      implicit imageFlowNativeLibrary: ImageFlowNative,
      context: Pointer,
  ): (Array[Byte], Int) = {
    val errorBuffer: Array[Byte]          = new Array[Byte](offset)
    val bytesWritten: LongLongByReference = new LongLongByReference()
    imageFlowNativeLibrary.imageflow_context_error_write_to_buffer(context, errorBuffer, offset, bytesWritten)
    (errorBuffer, bytesWritten.intValue())
  }

  def getError(offset: Int = 1024)(
      implicit imageFlowNativeLibrary: ImageFlowNative,
      context: Pointer,
  ): String = {
    val (errorBuffer, _): (Array[Byte], Int) = getErrorBytes()
    errorBuffer.map(_.toChar).mkString
  }

  def destroyContext()(implicit imageFlowNativeLibrary: ImageFlowNative, context: Pointer): Boolean =
    imageFlowNativeLibrary.imageflow_context_begin_terminate(context)
}
