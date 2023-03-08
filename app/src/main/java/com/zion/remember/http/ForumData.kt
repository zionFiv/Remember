package com.zion.remember.http

data class ForumData(
    var page : Int,
    var totalCount : Int,
    var totalPage : Int,
    var limit : Int,
    val list : List<ForumList>,
)

data class ForumList(
    val content : String,
    val updateTime :String,
)

data class WeatherVo(
    val address : String,
    val cityCode : String,
    val temp : String,
    val weather : String,
    val windDirection : String,
    val windPower : String,
    val humidity : String,
    val reportTime : String,
)
