package com.zion.remember.http

import com.google.gson.GsonBuilder
import com.zion.remember.util.LogUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jsoup.Jsoup
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

class RManager {
    lateinit var retrofit: Retrofit
    public fun init() {
        retrofit = Retrofit.Builder().baseUrl("https://www.xxbiqudu.com")
            .addConverterFactory(ScalarsConverterFactory.create())
            .build()
    }

    public fun request() {
        CoroutineScope(Dispatchers.Default).launch {
            val abc = retrofit.create(BookService::class.java)
                .getChapterInfo("https://www.xxbiqudu.com/152_152375/178863183.html")
           val edf =  abc.execute()
            val doc  = Jsoup.parse(edf.body())
//            val list = doc.getElementById("list-chapterAll").getElementsByTag("dd")
            doc.allElements.forEach {
                it.id()
                LogUtil.d("edf :  ${it.id()}\n ${it.tag()} \n ${it.text()} \n")
                LogUtil.d("edf 2 ${it.allElements.size} ${it.allElements.text()}")
            }


        }

    }
}