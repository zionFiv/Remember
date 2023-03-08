package com.zion.remember.http

import retrofit2.Call
import retrofit2.http.*

interface ForumService {
    //return 使用call & Response的区别
    //call 和 suspend不能共用？
    @Headers("base_url:mxnzp")
    @POST("api/jokes/list/random")
    fun getJokeRandomList(
        @Query("app_id") app_id: String = MXN_APP_ID,
        @Query("app_secret") app_secret: String = MXN_APP_SECRET
    ): Call<BaseData<ForumList>>

    @Headers("base_url:mxnzp")
    @GET("api/jokes/list")
    fun getJokeList(
        @Query("page") id: String,
        @Query("app_id") app_id: String = MXN_APP_ID,
        @Query("app_secret") app_secret: String = MXN_APP_SECRET
    ): Call<BaseData<ForumData>>

    @Headers("base_url:mxnzp")
    @GET("api/weather/current/{cityName}")
    fun getCityWeather(
        @Path("cityName") cityName: String,
        @Query("app_id") app_id: String = MXN_APP_ID,
        @Query("app_secret") app_secret: String = MXN_APP_SECRET
    ): Call<BaseData<WeatherVo>>

}