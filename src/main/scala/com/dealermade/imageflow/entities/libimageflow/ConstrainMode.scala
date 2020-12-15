package com.dealermade.imageflow.entities.libimageflow

/**
  * An Enum class which represent the constrain
  * Check <a href="https://docs.imageflow.io/json/constrain.html">constrain</a> for more information
  * @param value the value of the constrain
  */
sealed abstract class ConstrainMode(val value: String)

/**
  * "distort" the image to exactly the given dimensions.
  * If only one dimension is specified, behaves like `fit`.
  */
case object Distort extends ConstrainMode("distort")

/**
  * Ensure the result fits within the provided dimensions. No upscaling.
  */
case object Within extends ConstrainMode("within")

/**
  * Fit the image within the dimensions, upscaling if needed
  */
case object Fit extends ConstrainMode("fit")

/**
  * Ensure the image is larger than the given dimensions
  */
case object LargerThan extends ConstrainMode("larger_than")

/**
  * Crop to desired aspect ratio if image is larger than requested, then downscale. Ignores smaller images.
  * If only one dimension is specified, behaves like `within`.
  */
case object WithinCrop extends ConstrainMode("within_crop")

/**
  * Crop to desired aspect ratio, then downscale or upscale to fit.
  * If only one dimension is specified, behaves like `fit`.
  */
case object FitCrop extends ConstrainMode("fit_crop")

/**
  * Crop to desired aspect ratio, no upscaling or downscaling. If only one dimension is specified, behaves like Fit.
  */
case object AspectCrop extends ConstrainMode("aspect_crop")

/**
  * Pad to desired aspect ratio if image is larger than requested, then downscale. Ignores smaller images.
  * If only one dimension is specified, behaves like `within`
  */
case object WithinPad extends ConstrainMode("within_pad")

/**
  * Pad to desired aspect ratio, then downscale or upscale to fit
  * If only one dimension is specified, behaves like `fit`.
  */
case object FitPad extends ConstrainMode("fit_pad")
