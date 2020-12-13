package com.dealermade.imageflow.entities

import com.dealermade.imageflow.entities.ImageJob.Commands
import com.dealermade.imageflow.{IoMode, Lifetime}
import com.google.gson.Gson
import scala.annotation.tailrec

case class ImageJob(filePath: String, ioMode: IoMode, lifetime: Lifetime, commands: String)

object ImageJob {
	type Commands = (String, Option[ImageCommand])
}

class ImageJobBuilder(filePath: String, ioMode: IoMode, lifetime: Lifetime, commands: String = "") {
	def copy(filePath: String = this.filePath,
	         ioMode: IoMode= this.ioMode,
	         lifetime: Lifetime = this.lifetime,
	         commands: String = this.commands) = ImageJob(filePath, ioMode, lifetime, commands)

	def flip(orientation: ORIENTATION): ImageJob = {
		assert(Seq(HORIZONTAL, VERTICAL).contains(orientation), "Orientation should be one of HORIZONTAL or VERTICAL")
		val newCommands: String = s"${this.commands},${decode(Vector((s"flip_${orientation.value}", Option.empty)))}"
		copy(commands = newCommands)
	}

	def rotate(rotation: Int): ImageJob = {
		assert(Seq(90, 180, 270).contains(rotation), "Rotation should be one of 90, 180 or 270")
		val newCommands: String = s"${this.commands},${decode(Vector((s"rotate_$rotation", Option.empty)))}"
		copy(commands = newCommands)
	}

	def transpose: ImageJob = {
		val newCommands: String = s"${this.commands},${decode(Vector(("transpose", Option.empty)))}"
		copy(commands = newCommands)
	}

	def resize(width: Int, height: Int): ImageJob = {
		val newCommands: String = "" // this.commands :+ ""
		copy(commands = newCommands)
	}

	def quality(quality: Int): ImageJob = {
		val newCommands: String = "" // this.commands :+ ("quality", quality)
		copy(commands = newCommands)
	}

	def decode(commands: Vector[Commands]): ImageJob = {
		@tailrec
		def decodeRecursive(encodingCommands: Vector[Commands], accumulator: String): String = encodingCommands match {
			case (x: Commands) +: IndexedSeq() => s"$accumulator${encodeCommands(x)}"
			case (x: Commands) +: (tail: Vector[Commands]) => decodeRecursive(tail, s"$accumulator${encodeCommands(x)},")
			case _ => accumulator
		}
		val newCommands: String = decodeRecursive(commands, "")
		copy(commands = newCommands)
	}

	private[this] def encodeCommands(commands: Commands): String = {
		val gson: Gson = new Gson()
		commands._2.fold {
			gson.toJson(commands._1)
		}{ maybeCommands => {
			val data: ImageRequest = ImageRequest(commands._1, maybeCommands)
			gson.toJson(data)
		}
		}.replace(""""""", """/"""")
	}
}