package com.dealermade.imageflow.entities

sealed abstract class ORIENTATION(val value: Char)
case object HORIZONTAL extends ORIENTATION(value = 'h')
case object VERTICAL extends ORIENTATION(value = 'v')

trait ImageCommand {}
