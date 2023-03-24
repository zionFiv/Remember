package com.zion.remember.news

data class NewsListVo(
    val title: String,
    val imgList: List<String>,
    val source: String,
    val newsId: String,
    val digest: String,
    val postTime: String,
    val videoList: List<String>,
    )

data class NewsDetailVo(
    val title: String,
    val imgList: List<String>,
    val content: String,
    val source: String,
    val ptime: String,
    val docid: String,
    val cover: String,
)

data class NewsTypeVo(
    val typeId : String,
    val typeName : String
)