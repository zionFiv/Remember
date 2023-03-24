package com.zion.remember.news

import android.os.Bundle
import android.text.Html
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.zion.remember.R
import com.zion.remember.databinding.ActivityNewsDetailPageBinding
import com.zion.remember.http.BaseData
import com.zion.remember.http.ForumService
import com.zion.remember.http.RManager
import com.zion.remember.util.StatusBarUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NewsDetailPageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityNewsDetailPageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        StatusBarUtil.setStatusBarBg(this, R.color.white,true)

        val id = intent.getStringExtra("newsId")
        val cover = intent.getStringExtra("newsImage")
        val title = intent.getStringExtra("newsTitle")

        binding.newsDetailImg.transitionName="news_image"
        binding.newsDetailTitle.transitionName="news_title"

        Glide.with(this@NewsDetailPageActivity).load(cover).centerCrop().into(binding.newsDetailImg)
        binding.newsDetailTitle.text = title

//        binding.newsDetailContent.transitionName="news_content"
        RManager.retrofit.create(ForumService::class.java).getNewsDetail(id ?: "").enqueue(
            object : Callback<BaseData<NewsDetailVo>>{
                override fun onResponse(call: Call<BaseData<NewsDetailVo>>, response: Response<BaseData<NewsDetailVo>>) {
                    val data = response.body()?.data
                    data?.let {
                        binding.newsDetailContent.text = Html.fromHtml(it.content,
                            Html.FROM_HTML_MODE_LEGACY)


                    }
                }

                override fun onFailure(call: Call<BaseData<NewsDetailVo>>, t: Throwable) {
                }

            }
        )
    }
}