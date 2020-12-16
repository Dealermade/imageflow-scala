package com.dealermade.imageflow.libimageflow

import zio.json.jsonField

/**
 * An entity to be used in Image flow API
 *
 * @param ioID the io mode if for the current context/image
 * @param direction should be in or out
 * @param io should be placeholder // TODO check it out
 */
case class IO(
  @jsonField("io_id") ioID: Int,
  direction: Option[String],
  io: Option[String] = Some("placeholder")
)
