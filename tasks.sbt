import sbt.complete.Parsers.spaceDelimited

lazy val useOS = inputKey[Unit]("Run with a specific OS")

/**
 * A Sbt Task to download a dll for a specific OS
 */
useOS := Def.inputTaskDyn {
	val args: Seq[String] = spaceDelimited("<arg>").parsed
	(Compile / runMain).toTask(s""" com.dealermade.imageflow.tasks.RunUsingOSTask --os "${args.mkString(" ")}"""")
}.evaluated