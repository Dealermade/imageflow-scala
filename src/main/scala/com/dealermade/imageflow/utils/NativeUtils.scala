package com.dealermade.imageflow.utils

object NativeUtils {

  /**
	 * Get the resources PATH
	 * @return the path to the current source resources folder
	 */
  def getPathToApp: String = {
    import better.files._
    (file"src" / "main" / "resources").path.toString
  }

  def loadDLL(library: String): Unit = System.load(getPathToApp + "/" + library)
}
