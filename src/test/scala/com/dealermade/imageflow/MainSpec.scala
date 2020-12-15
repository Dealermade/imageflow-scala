package com.dealermade.imageflow.jobs

import com.dealermade.imageflow.entities.libimageflow.{ AspectCrop, LossyPNG, PNGQuant }
import com.dealermade.imageflow.jobs.{ ImageJob, ImageJobBuilder }
import com.dealermade.imageflow.native._
import jnr.ffi.Pointer

object MainSpec extends App {

  implicit val imageFlowNativeLibrary: ImageFlowNative = ImageFlowNative()

  val version: ImageFlowVersion = ImageFlowNative.getVersion()
  val isCompatible: Boolean     = ImageFlowNative.isCompatible(version)

  println(
    s"Imageflow Native: version major ${version.versionMajor} & version minor ${version.versionMinor} is ${if (isCompatible) "compatible"
    else "not comptabile"}"
  )

  //  **Contexts are not thread-safe!** Once you create a context, *you* are responsible for ensuring that it is never involved in two overlapping API calls.
  // Should add a lock
  val context: Pointer = ImageFlowNative.createContext(version)

  val imageJob: ImageJob =
    ImageJobBuilder(getClass.getResource("/jpeg2000-home.jpg").getPath, None, OutlivesContext)(
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
  } else {
    throw new RuntimeException(s"An error occurred while writing the image output")
  }

}
