package com.dealermade.imageflow

import sbt.{ Def, _ }

object Dependencies {

	val scalaTestVersion: String = "3.2.2"
	val scalaJHttpVersion: String = "2.4.2"
	val gsonVersion: String = "2.8.6"
	val betterFilesVersion: String = "3.9.1"
	val jnrVersion: String = "2.2.1"

	object Scala {
		val version: String = "2.13.4"
	}

	def scalaJHttp: Def.Initialize[ModuleID] = Def.setting {
		"org.scalaj" %% "scalaj-http" % scalaJHttpVersion
	}

	def betterFiles: Def.Initialize[ModuleID] = Def.setting {
		"com.github.pathikrit" %% "better-files" % betterFilesVersion
	}

	def gson: Def.Initialize[ModuleID] = Def.setting {
		"com.google.code.gson" % "gson" % gsonVersion
	}

	def jnrFFI: Def.Initialize[ModuleID] = Def.setting {
		"com.github.jnr" % "jnr-ffi" % jnrVersion
	}

	def dependencies(deps: ModuleID*): Seq[ModuleID] = deps

	def scalaTest: Def.Initialize[ModuleID] = Def.setting {
		"org.scalatest" %% "scalatest" % scalaTestVersion
	}

	def testDependencies(deps: ModuleID*): Seq[ModuleID] = deps map (_ % "test")

}
