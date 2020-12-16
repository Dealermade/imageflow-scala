package com.dealermade.imageflow.libimageflow

import com.dealermade.imageflow.native.ImageFlowNative
import jnr.ffi.byref.{ LongLongByReference, PointerByReference }
import jnr.ffi.{ Memory, Pointer, Runtime }
import zio.Task
import zio.json._

import java.nio.file.{ Files, Path }

protected[imageflow] case class ImageFlowProcessJob(
  inputImagePath: Path,
  outputImagePath: Path,
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

  def loadInputImageIntoMemory(): Task[Boolean] =
    for {
      buffer: Array[Byte] <- Task.effect(Files.readAllBytes(inputImagePath))
      offset: Int = buffer.length
      pointer <-
        Task.effect(Memory.allocateDirect(Runtime.getSystemRuntime, offset.toInt, true))
      _ <- Task.effect(pointer.put(0, buffer, 0, offset))
      successfullyAddedToBuffer: Boolean <- Task.effect(
        imageFlowNativeLibrary.imageflow_context_add_input_buffer(
          context,
          IoMode.None.value,
          pointer,
          offset,
          lifetime.value
        )
      )
      _ <-
        Task
          .fail(new Throwable(ImageFlowNative.getError()))
          .when(!successfullyAddedToBuffer)
    } yield successfullyAddedToBuffer

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

  private def processImage() = {
    val commands: Array[Byte] = getCommandsAsBytes()
    val jsonResponse: ImageFlowResponseStruct =
      imageFlowNativeLibrary.imageflow_context_send_json(
        context,
        Execute.api,
        commands,
        commands.length
      )
    ImageFlowResponse.parse(jsonResponse.data.get())
  }

  def writeOutputImage() =
    for {
      _ <- Task.effect(
        imageFlowNativeLibrary.imageflow_context_add_output_buffer(context, ioMode.value)
      )
      imageProcessingResponse <- processImage()
      pointByRef = new PointerByReference
      bytesWritten = new LongLongByReference(0)
      _ <- Task.effect(
        imageFlowNativeLibrary.imageflow_context_get_output_buffer_by_id(
          context,
          ioMode.value,
          pointByRef,
          bytesWritten
        )
      )
      pointer: Pointer = pointByRef.getValue
      response: Array[Byte] = new Array[Byte](bytesWritten.intValue())
      _ <- Task.effect(pointer.get(0, response, 0, bytesWritten.intValue()))
      _ <- Task.effect(Files.write(outputImagePath, response))
    } yield ()
}
