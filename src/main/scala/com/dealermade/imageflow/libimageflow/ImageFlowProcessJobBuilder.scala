package com.dealermade.imageflow.libimageflow

import com.dealermade.imageflow.native.ImageFlowNative
import jnr.ffi.Pointer
import zio.Task

import java.nio.file.Path

/**
 * An image flow builder which can build the image with a specific steps
 * Warning!! You should specify the steps in order, for example decode, constrain, rotate, encode
 *
 * @param filePath the file path to the image
 * @param ioMode the IO mode
 * @param lifetime the lifetime used in the native If you specify `OutlivesFunctionCall`, then the buffer will be copied.
 * @param io the list of the IOs
 * @param steps the steps to be executed on the image
 * @param security the security schema to be run in image flow
 * @param imageFlowNative the image flow native library
 * @param context the pointer to the created image flow context
 */
protected[imageflow] case class ImageFlowProcessJobBuilder(
  inputImagePath: Path,
  outputImagePath: Path = Path.of("/"),
  ioMode: IoMode = IoMode.ReadWriteSeekable,
  lifetime: ImageBufferLifetime = ImageBufferLifetime.OutlivesContext,
  io: Set[IO] = Set.empty,
  steps: Vector[ImageFlowStep] = Vector.empty,
  security: Option[Security] = None
)(
  implicit imageFlowNative: ImageFlowNative,
  implicit val context: Pointer
) {
  def buildLoadAndResize(): Task[Unit] = {
    val imageFlowProcessJob: ImageFlowProcessJob = build()
    val t0 = System.nanoTime()
    for {
      _ <- imageFlowProcessJob.loadInputImageIntoMemory()
      _ <- Task.effect(
        println(
          s"Time after load input into memory: ${((System.nanoTime() - t0) / 1_000_000_000.0)} seconds"
        )
      )
      _ <- imageFlowProcessJob.writeOutputImage()
      _ <- Task.effect(
        println(
          s"Time after write output file: ${((System
            .nanoTime() - t0) / 1_000_000_000.0)} seconds"
        )
      )
    } yield ()
  }

  def build(): ImageFlowProcessJob =
    ImageFlowProcessJob(
      inputImagePath = inputImagePath,
      outputImagePath = outputImagePath,
      ioMode = ioMode,
      lifetime = lifetime,
      io = io + IO(IoMode.None.value, Some(ImageFlowIn.value)),
      steps = steps,
      security = security
    )

  def flip(orientation: Orientation): ImageFlowProcessJobBuilder = {
    val newSteps: Vector[ImageFlowStep] = addCommand(
      ImageFlowStep.Flip(s"flip_${orientation.value}")
    )
    copy(steps = newSteps)
  }

  def rotate(rotation: Int): ImageFlowProcessJobBuilder = {
    assert(
      Vector(90, 180, 270).contains(rotation),
      s"The rotation $rotation is not yet supported."
    )
    val newSteps: Vector[ImageFlowStep] = addCommand(
      ImageFlowStep.Rotation(s"rotate_$rotation")
    )
    copy(steps = newSteps)
  }

  def transpose: ImageFlowProcessJobBuilder = {
    val newSteps: Vector[ImageFlowStep] = addCommand(ImageFlowStep.Transpose("transpose"))
    copy(steps = newSteps)
  }

  def constrain(
    mode: Option[ConstrainMode],
    width: Option[Int] = None,
    height: Option[Int] = None,
    gravity: Option[Gravity] = None,
    canvasColor: Option[String] = None,
    hints: Option[Hints] = None
  ): ImageFlowProcessJobBuilder = {
    // We should use Option.empty[String] because None doesn't require any type parameters
    val constrainMode: Option[String] = mode.fold(Option.empty[String]) {
      constrainModeString =>
        Some(constrainModeString.value)
    }
    val constrainStep: ConstrainStep =
      ConstrainStep(constrainMode, width, height, gravity, canvasColor, hints)
    val constrain: ImageFlowStep.Constrain = ImageFlowStep.Constrain(constrainStep)
    val newSteps: Vector[ImageFlowStep] = addCommand(constrain)
    copy(steps = newSteps)
  }

  def decode(
    commands: Vector[ImageFlowDecodeStep] = Vector.empty
  ): ImageFlowProcessJobBuilder = {
    val maybeCommands: Option[Vector[ImageFlowDecodeStep]] =
      if (commands.isEmpty) None else Some(commands)
    val decode: ImageFlowStep.Decode =
      ImageFlowStep.Decode(IoMode.None.value, maybeCommands)
    val newSteps: Vector[ImageFlowStep] = addCommand(decode)
    copy(steps = newSteps, io = io + IO(IoMode.None.value, Some(ImageFlowIn.value)))
  }

  def encode(presets: EncodePreset): ImageFlowProcessJobBuilder = {
    val encode: ImageFlowStep.Encode = ImageFlowStep.Encode(ioMode.value, presets)
    val newSteps: Vector[ImageFlowStep] = addCommand(encode)
    copy(steps = newSteps, io = io + IO(ioMode.value, Some(ImageFlowOut.value)))
  }

  private[this] def addCommand(step: ImageFlowStep): Vector[ImageFlowStep] =
    this.steps :+ step

}
