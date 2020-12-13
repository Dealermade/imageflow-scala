package com.dealermade.imageflow.tasks

//import com.fasterxml.jackson.databind.json.JsonMapper
//import com.fasterxml.jackson.module.scala.DefaultScalaModule
import scalaj.http._

object RunUsingOSTask extends App {
//	val mapper: JsonMapper = JsonMapper.builder()
//		.addModule(DefaultScalaModule)
//		.build()
//
//	val response: HttpResponse[String] = Http("https://api.github.com/repos/imazen/imageflow/releases")
//		.header("Accept", "application/vnd.github.v3+json")
//    		.asString
//
//	val data = mapper.readValue(response.body, classOf[Seq[Github]])
//	data(1).assets.filter(_.browserDownloadUrl.contains("win-x86_64.zip")).take(1).foreach(downloadAsset)
//

	private def downloadAsset(githubAsset: GithubAsset): Unit = {
//		val output: Seq[Byte] = Http(githubAsset.browserDownloadUrl).asBytes.body.takeWhile(-1 !=)

	}
}
