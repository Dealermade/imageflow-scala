package com.dealermade.imageflow.libimageflow

import com.dealermade.imageflow.native.ImageFlowNative
import jnr.ffi.byref.{ LongLongByReference, PointerByReference }
import jnr.ffi.{ Memory, Pointer, Runtime }
import zio.json._

import java.nio.file.{ Files, Path, Paths }
import scala.reflect.io.File

protected[imageflow] case class ImageFlowProcessJob(
  outputFilePath: Path,
  ioMode: IoMode = IoMode.ReadWriteSeekable,
  lifetime: ImageBufferLifetime,
  io: Set[IO],
  steps: Vector[ImageFlowStep],
  security: Option[Security]
)(
  implicit val imageFlowNativeLibrary: ImageFlowNative,
  implicit val context: Pointer
) {

  def getCommandJson(): String = {
    val framwise: Framwise = Framwise(steps)
    val imageFlow: ImageFlow = ImageFlow(io.toVector, framwise, None)
    imageFlow.toJson
  }

  def getCommandsAsBytes(): Array[Byte] =
    getCommandJson().getBytes

  def addImageToContext(): Either[Boolean, String] = {
    val buffer: Array[Byte] = Files.readAllBytes(outputFilePath)
    val offset: Int = buffer.length
    val pointer = Memory.allocateDirect(Runtime.getSystemRuntime, offset.toInt, true)
    pointer.put(0, buffer, 0, offset)
    val isSucceeded: Boolean =
      imageFlowNativeLibrary.imageflow_context_add_input_buffer(
        context,
        IoMode.None.value,
        pointer,
        offset,
        lifetime.value
      )
    if (isSucceeded)
      Left(isSucceeded)
    else {
      val error: String = ImageFlowNative.getError()
      // Maybe throw an exception here
      Right(error)
    }
  }

  def getImageInfo(ioID: Int): Either[String, ImageInfoResponse] = {
    import com.dealermade.imageflow.libimageflow.ImageFlow._

    val io: IO = IO(ioID, None, None)
    val ioCommands: Array[Byte] = io.toJson.getBytes
    val jsonResponse: ImageFlowResponseStruct =
      imageFlowNativeLibrary.imageflow_context_send_json(
        context,
        GetImageInfo.api,
        ioCommands,
        ioCommands.length
      )
    jsonResponse.data.get().fromJson[ImageInfoResponse]
  }

  private def processImage(): Either[String, JobResultResponse] = {
    val commands: Array[Byte] = getCommandsAsBytes()
    println(commands.map(_.toChar).mkString)
    val jsonResponse: ImageFlowResponseStruct =
      imageFlowNativeLibrary.imageflow_context_send_json(
        context,
        Execute.api,
        commands,
        commands.length
      )
    ImageFlowResponse.parse(jsonResponse.data.get())
  }

  def writeOutputImage(filePath: Path) = {
    imageFlowNativeLibrary.imageflow_context_add_output_buffer(context, ioMode.value)
    val imageProcessingResponse = processImage()
    println(imageProcessingResponse)
    val pointByRef = new PointerByReference
    val bytesWritten = new LongLongByReference(0)
    imageFlowNativeLibrary.imageflow_context_get_output_buffer_by_id(
      context,
      ioMode.value,
      pointByRef,
      bytesWritten
    )
    val pointer: Pointer = pointByRef.getValue
    val response: Array[Byte] = new Array[Byte](bytesWritten.intValue())
    pointer.get(0, response, 0, bytesWritten.intValue())
    Files.write(filePath, response)
  }
}
