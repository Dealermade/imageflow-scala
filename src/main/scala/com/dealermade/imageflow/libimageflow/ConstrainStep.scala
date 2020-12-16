package com.dealermade.imageflow.libimageflow

import zio.json.jsonField

/**
 *
  * @param mode check https://docs.imageflow.io/json/constrain.html#constraint-modes
 * @param width The width constraint in pixels
 * @param height The height constraint in pixels
 * @param gravity determines how the image is anchored when cropped or padded. {x: 0, y: 0}
 *                represents top-left, {x: 50, y: 50} represents center, {x:100, y:100}
 *                represents bottom-right. Default: center
 * @param canvasColor See Color (https://docs.imageflow.io/json/constrain.html#colors). The color of padding added to the image.
 * @param hints See https://docs.imageflow.io/json/constrain.html#resampling-hints
 * For more information check https://docs.imageflow.io/json/constrain.html
 */
case class ConstrainStep(
  mode: Option[String] = None,
  @jsonField("w") width: Option[Int] = None,
  @jsonField("h") height: Option[Int] = None,
  gravity: Option[Gravity] = None,
  @jsonField("canvas_color") canvasColor: Option[String] = None,
  hints: Option[Hints] = None
)
