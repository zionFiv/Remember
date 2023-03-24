package com.zion.remember.news

import androidx.collection.arrayMapOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.zion.remember.http.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NewsListViewModel : ViewModel() {
    private val _types = arrayListOf(
        NewsTypeVo("511", "军事"),
        NewsTypeVo("512", "时尚"),
        NewsTypeVo("514", "股票"),
        NewsTypeVo("515", "游戏"),
        NewsTypeVo("518", "要闻"),
        NewsTypeVo("521", "头条"),
    )

    val types get() = _types

    private val _newsLiveData = MutableLiveData<MutableList<NewsListVo>>()

    val newsLiveData: LiveData<MutableList<NewsListVo>>
        get() = _newsLiveData

    fun getNewsList(typeId: String, page: Int) {
        RManager.retrofit.create(ForumService::class.java).getNewsList(typeId, page).enqueue(
            object : Callback<BaseData<MutableList<NewsListVo>>> {
                override fun onResponse(
                    call: Call<BaseData<MutableList<NewsListVo>>>,
                    response: Response<BaseData<MutableList<NewsListVo>>>
                ) {
                    response.body()?.data?.let {
                        _newsLiveData.postValue(it)

                    }
                }

                override fun onFailure(
                    call: Call<BaseData<MutableList<NewsListVo>>>,
                    t: Throwable
                ) {

                }

            }
        )
    }

}