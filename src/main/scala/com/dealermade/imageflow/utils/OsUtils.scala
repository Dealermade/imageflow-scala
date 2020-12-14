package com.dealermade.imageflow.utils

object OsUtils {
  lazy val osName: String = System.getProperty("os.name").toLowerCase

  def dllByOS(version: String): Map[String, (String, String)] = Map(
    "win"   -> (s"https://github.com/anshulrgoyal/imageflow/releases/download/$version/imageflow.zip", "imageflow.dll"),
    "linux" -> (s"https://github.com/anshulrgoyal/imageflow/releases/download/$version/libimageflow_linux.zip", "libimageflow.a"),
    "macos" -> (s"https://github.com/anshulrgoyal/imageflow/releases/download/$version/libimageflow_mac.zip", "libimageflow.a"),
  )

  def getDLLByOS(version: String): (String, String) = osName match {
    case os if os.contains("win")                                             => dllByOS(version)("win")
    case os if os.contains("mac")                                             => dllByOS(version)("macos")
    case os if os.contains("nix") || os.contains("nux") || os.contains("aix") => dllByOS(version)("linux")
    case _                                                                    => throw new IllegalArgumentException(s"The OS $osName is not yet supported.")
  }
}
