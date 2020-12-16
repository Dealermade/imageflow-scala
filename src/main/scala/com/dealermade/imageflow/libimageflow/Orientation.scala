package com.dealermade.imageflow.libimageflow

sealed abstract class Orientation(val value: Char)
case object Horizontal extends Orientation(value = 'h')
case object Vertical extends Orientation(value = 'v')
