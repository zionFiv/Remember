package com.zion.remember.http

import com.google.gson.GsonBuilder
import com.zion.remember.util.LogUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import org.jsoup.Jsoup
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit

object RManager {

    private val retrofit by lazy {
        Retrofit.Builder().baseUrl(FORUM_BASE_URL)
            .client(getOkHttpClient())
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

     private fun getOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder().addInterceptor(BaseUrlIntercept())
            .readTimeout(15, TimeUnit.SECONDS)
            .connectTimeout(15, TimeUnit.SECONDS).build()
    }

     fun getForumList(callback: Callback<BaseData<ForumData>>) {
        retrofit.create(ForumService::class.java).getJokeList("1").enqueue(
           callback
        )

    }

    fun getCityWeather(cityName : String, callback: Callback<BaseData<WeatherVo>>) {
        retrofit.create(ForumService::class.java).getCityWeather(cityName).enqueue(callback)
    }

    public fun request() {
        CoroutineScope(Dispatchers.Default).launch {
            val abc = retrofit.create(BookService::class.java)
                .getChapterInfo("https://www.xxbiqudu.com/152_152375/178863183.html")
            val edf = abc.execute()
            val doc = Jsoup.parse(edf.body())
//            val list = doc.getElementById("list-chapterAll").getElementsByTag("dd")
            doc.allElements.forEach {
                it.id()
                LogUtil.d("edf :  ${it.id()}\n ${it.tag()} \n ${it.text()} \n")
                LogUtil.d("edf 2 ${it.allElements.size} ${it.allElements.text()}")
            }


        }

    }
}