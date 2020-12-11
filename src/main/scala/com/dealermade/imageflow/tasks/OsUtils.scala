package com.dealermade.imageflow.tasks

object OsUtils {
	lazy val osName: String = System.getProperty("os.name").toLowerCase
	lazy val dllByOS: Map[String, (String, String)] = Map(
		"win" -> ("https://github.com/anshulrgoyal/imageflow/releases/download/v1.4.0-rc40/imageflow.zip", "imageflow.dll"),
		"linux" -> ("https://github.com/anshulrgoyal/imageflow/releases/download/v1.4.0-rc40/libimageflow_linux.zip", "libimageflow.a"),
		"macos" -> ("https://github.com/anshulrgoyal/imageflow/releases/download/v1.4.0-rc40/libimageflow_mac.zip", "libimageflow.a"),
	)

	def getDLLByOS: (String, String) = osName match {
		case os if os.contains("win") => dllByOS("win")
		case os if os.contains("mac") => dllByOS("macos")
		case os if os.contains("nix") || os.contains("nux") || os.contains("aix") => dllByOS("linux")
		case _ => throw new IllegalArgumentException(s"The OS $osName is not yet supported.")
	}
}
