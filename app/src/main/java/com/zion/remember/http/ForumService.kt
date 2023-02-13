package com.zion.remember.http

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query

interface ForumService {
    @Headers("base_url:mxnzp")
    @POST("api/jokes/list/random")
    fun getJokeRandomList( @Query("app_id") app_id: String = MXN_APP_ID,
                           @Query("app_secret") app_secret: String = MXN_APP_SECRET): Call<String>

    @Headers("base_url:mxnzp")
    @GET("api/jokes/list")
    fun getJokeList(
        @Query("page") id: String,
        @Query("app_id") app_id: String = MXN_APP_ID,
        @Query("app_secret") app_secret: String = MXN_APP_SECRET
    ): Call<String>

}