package com.dealermade.imageflow

import com.dealermade.imageflow.entities.libimageflow.{
  ImageFlowVersion,
  LossyPNG,
  PNGQuant
}
import com.dealermade.imageflow.jobs.ImageJobBuilder
import com.dealermade.imageflow.native._
import jnr.ffi.Pointer

object MainSpec extends App {

  val imageJob: ImageJob =
    ImageJobBuilder(
      getClass.getResource("/jpeg2000-home.jpg").getPath,
      None,
      OutlivesContext
    )(
      imageFlowNativeLibrary,
      context
    ).decode(None.value)
      // make mode as enum
      .constrain(mode = Some(AspectCrop), width = Some(500), height = Some(350))
      .encode(ReadSequential.value, LossyPNG(PNGQuant()))
      .build

  val result: Either[Boolean, String] = imageJob.addImageToContext()

  result match {
    case Left(_) => println("Image buffer added to the context with success :D")
    case Right(error) =>
      throw new RuntimeException(
        s"An error occurred while adding image buffer to the context $error"
      )
  }

  val imageInfo = imageJob.getImageInfo(None.value)
  println(imageInfo)

  val isDone: Boolean = imageJob.addOutputBuffer(ReadSequential)
  if (isDone) {
    val imageProcessingResponse = imageJob.processImage()
    println(imageProcessingResponse)
    imageJob.writeOutputImageToFile(ReadSequential.value, "src/test/resources/test.png")
  } else
    throw new RuntimeException(s"An error occurred while writing the image output")

}
