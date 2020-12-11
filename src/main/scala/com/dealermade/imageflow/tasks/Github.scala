package com.dealermade.imageflow.tasks

case class Github(url: String, assets: Seq[GithubAsset])
case class GithubAsset(url: String, browserDownloadUrl: String)