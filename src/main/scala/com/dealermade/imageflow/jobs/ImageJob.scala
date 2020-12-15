package com.dealermade.imageflow.jobs

import zio.json._

import com.dealermade.imageflow.entities._
import com.dealermade.imageflow.native.{ Execute, GetImageInfo, ImageFlowNative, In, IoMode, Lifetime, Out }
import jnr.ffi.byref.{ LongLongByReference, PointerByReference }
import jnr.ffi.{ Memory, Pointer, Runtime }

import scala.reflect.io.File

protected[jobs] case class ImageJob(
    filePath: String,
    ioMode: IoMode,
    lifetime: Lifetime,
    io: Set[IO],
    steps: Vector[Step],
    security: Option[Security]
)(
    implicit val imageFlowNativeLibrary: ImageFlowNative,
    implicit val context: Pointer
) {

  def getFileStream: Array[Byte] =
    File(filePath).toByteArray()

  def getCommands: Array[Byte] = {
    val framwise: Framwise   = Framwise(steps)
    val imageFlow: ImageFlow = ImageFlow(io.toVector, framwise, None)
    import com.dealermade.imageflow.entities.ImageFlow._
    imageFlow.toJson.getBytes
  }

  def addImageToContext(): Either[Boolean, String] = {
    val buffer: Array[Byte] = getFileStream
    val offset: Int         = buffer.length
    val pointer             = Memory.allocateDirect(Runtime.getSystemRuntime, offset.toInt, true)
    pointer.put(0, buffer, 0, offset)
    val isSucceeded: Boolean =
      imageFlowNativeLibrary.imageflow_context_add_input_buffer(context, ioMode.value, pointer, offset, lifetime.value)
    if (isSucceeded) {
      Left(isSucceeded)
    } else {
      val error: String = ImageFlowNative.getError()
      // Maybe throw an exception here
      Right(error)
    }
  }

  def addOutputBuffer(ioMode: IoMode): Boolean =
    imageFlowNativeLibrary.imageflow_context_add_output_buffer(context, ioMode.value)

  def getImageInfo(ioID: Int): Either[String, ImageFlowResponse] = {
    import com.dealermade.imageflow.entities.ImageFlow._

    val io: IO                  = IO(ioID, None, None)
    val ioCommands: Array[Byte] = io.toJson.getBytes
    val jsonResponse: ImageFlowResponseStruct =
      imageFlowNativeLibrary.imageflow_context_send_json(context, GetImageInfo.api, ioCommands, ioCommands.length)

    import com.dealermade.imageflow.entities.ImageFlowResponse._
    jsonResponse.data.get().fromJson[ImageFlowResponse]
  }

  def processImage(): Either[String, ImageFlowResponse] = {
    val commands: Array[Byte] = getCommands
    val jsonResponse: ImageFlowResponseStruct =
      imageFlowNativeLibrary.imageflow_context_send_json(context, Execute.api, commands, commands.length)
    jsonResponse.data.get().fromJson[ImageFlowResponse]
  }

  def writeOutputImage(ioID: Int): Array[Byte] = {
    val pointByRef   = new PointerByReference
    val bytesWritten = new LongLongByReference(0)
    imageFlowNativeLibrary.imageflow_context_get_output_buffer_by_id(context, ioID, pointByRef, bytesWritten)
    val pointer: Pointer      = pointByRef.getValue
    val response: Array[Byte] = new Array[Byte](bytesWritten.intValue())
    pointer.get(0, response, 0, bytesWritten.intValue())
    response
  }

  def writeOutputImageToFile(ioID: Int, filePath: String): File = {
    val imageBuffer: Array[Byte] = writeOutputImage(ioID)
    val file: File               = File(filePath)
    file.writeAll(imageBuffer.map(_.toChar).mkString)
    file
  }
}

/**
  * An image flow builder which can build the image with a specific steps
  * Warning!! You should specify the steps in order, for example decode, constrain, rotate, encode
  * @param filePath the file path to the image
  * @param ioMode the IO mode
  * @param lifetime the lifetime used in the native If you specify `OutlivesFunctionCall`, then the buffer will be copied.
  * @param io the list of the IOs
  * @param steps the steps to be executed on the image
  * @param security the security schema to be run in image flow
  * @param imageFlowNative the image flow native library
  * @param context the pointer to the created image flow context
  */
protected[dealermade] case class ImageJobBuilder(
    filePath: String,
    ioMode: IoMode,
    lifetime: Lifetime,
    io: Set[IO] = Set.empty,
    steps: Vector[Step] = Vector.empty,
    security: Option[Security] = None
)(
    implicit imageFlowNative: ImageFlowNative,
    implicit val context: Pointer
) {

  def build: ImageJob = ImageJob(filePath, ioMode, lifetime, io + IO(ioMode.value, Some(In.value)), steps, security)

  def flip(orientation: Orientation): ImageJobBuilder = {
    val newSteps: Vector[Step] = addCommand(Flip(s"flip_${orientation.value}"))
    copy(steps = newSteps)
  }

  def rotate(rotation: Int): ImageJobBuilder = {
    assert(Vector(90, 180, 270).contains(rotation), s"The rotation $rotation is not yet supported.")
    val newSteps: Vector[Step] = addCommand(Rotation(s"rotate_$rotation"))
    copy(steps = newSteps)
  }

  def transpose: ImageJobBuilder = {
    val newSteps: Vector[Step] = addCommand(Transpose("transpose"))
    copy(steps = newSteps)
  }

  def constrain(
      mode: Option[ConstrainMode],
      width: Option[Int] = None,
      height: Option[Int] = None,
      gravity: Option[Gravity] = None,
      canvasColor: Option[String] = None,
      hints: Option[Hints] = None
  ): ImageJobBuilder = {
    // We should use Option.empty[String] because None doesn't require any type parameters
    val constrainMode: Option[String] = mode.fold(Option.empty[String]) { constrainModeString =>
      Some(constrainModeString.value)
    }
    val constrainStep: ConstrainStep = ConstrainStep(constrainMode, width, height, gravity, canvasColor, hints)
    val constrain: Constrain         = Constrain(constrainStep)
    val newSteps: Vector[Step]       = addCommand(constrain)
    copy(steps = newSteps)
  }

  def decode(ioID: Int, commands: Vector[DecodeStep] = Vector.empty): ImageJobBuilder = {
    val maybeCommands: Option[Vector[DecodeStep]] = if (commands.isEmpty) None else Some(commands)
    val decode: Decode                            = Decode(ioID, maybeCommands)
    val newSteps: Vector[Step]                    = addCommand(decode)
    copy(steps = newSteps, io = io + IO(ioID, Some(In.value)))
  }

  def encode(ioID: Int, presets: Preset): ImageJobBuilder = {
    val encode: Encode         = Encode(ioID, presets)
    val newSteps: Vector[Step] = addCommand(encode)
    copy(steps = newSteps, io = io + IO(ioID, Some(Out.value)))
  }

  private[this] def addCommand(step: Step): Vector[Step] = this.steps :+ step

}
