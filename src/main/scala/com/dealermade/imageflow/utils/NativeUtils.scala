package com.dealermade.imageflow.utils

object NativeUtils {
	def getPathToApp: String = {
		import better.files._
		(file"src"/"main"/"resources"/"win-x86_64/").path.toString
	}

	def loadDLL(library: String): Unit = System.load(getPathToApp + "/" + library)
}
