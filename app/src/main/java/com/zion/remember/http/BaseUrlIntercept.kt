package com.zion.remember.http

import com.zion.remember.util.LogUtil
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.Response

class BaseUrlIntercept : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originRequest = chain.request()
        val oldUrl = originRequest.url()
        val builder = originRequest.newBuilder()
        val urlList = originRequest.headers(BASE_URL)
        if(urlList.isNullOrEmpty())
            return chain.proceed(originRequest)
        builder.removeHeader(BASE_URL)
        urlList.first().let {
            val baseUrl = when (it) {
                URL_MXN -> HttpUrl.parse(FORUM_BASE_URL)
                URL_XX -> HttpUrl.parse(XX_BASE_URL)
                else -> HttpUrl.parse(FORUM_BASE_URL)
            }
            baseUrl?.apply {
                val newUrl = oldUrl.newBuilder().scheme(scheme()).host(host())
                    .port(port()).build()
                LogUtil.d("response newUrl :${newUrl}")
                return chain.proceed(builder.url(newUrl).build())
            }
        }
        return chain.proceed(originRequest)
    }
}