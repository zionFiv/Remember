package com.zion.remember.news

import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.zion.remember.R
import com.zion.remember.adapter.RVHolder
import com.zion.remember.adapter.RvAdapter
import com.zion.remember.databinding.ActivityNewsListBinding
import com.zion.remember.util.MobileUtil
import com.zion.remember.util.StatusBarUtil
import androidx.core.util.Pair


class NewsListActivity : AppCompatActivity() {

    private var page = 1
    private var typeId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityNewsListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        StatusBarUtil.setStatusBarBg(this, R.color.purple_500)
        val viewModel = ViewModelProvider(this).get(NewsListViewModel::class.java)
        typeId = viewModel.types[0].typeId

        binding.newsTypeList.layoutManager =
            LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
        binding.newsTypeList.adapter =
            object : RvAdapter<NewsTypeVo>(this, viewModel.types, R.layout.item_news_list_type) {
                override fun convert(holder: RVHolder, data: NewsTypeVo, position: Int) {
                    holder.setText(R.id.news_type_list_title, data.typeName)
                    val titleText = holder.getView<TextView>(R.id.news_type_list_title)

                    titleText.setTextColor(ContextCompat.getColor(this@NewsListActivity, if(typeId == data.typeId) R.color.purple_200 else R.color.color_333333))

                    holder.itemView.setOnClickListener {
                        page = 1
                        typeId = data.typeId
                        binding.newsTypeList.adapter?.notifyDataSetChanged()
                        viewModel.getNewsList(data.typeId, page)
                    }
                }

            }
        binding.newsRefresh.setOnRefreshListener {
            page = 1
            viewModel.getNewsList(typeId, page)
        }
        binding.newsRefresh.setOnLoadMoreListener {
            page++
            viewModel.getNewsList(typeId, page)
        }
        binding.newsList.layoutManager = LinearLayoutManager(this)
        binding.newsList.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(
                outRect: Rect,
                view: View,
                parent: RecyclerView,
                state: RecyclerView.State
            ) {
                super.getItemOffsets(outRect, view, parent, state)
                outRect.top = MobileUtil.dp2px(10.0f).toInt()
            }
        })
        val listAdapter =
            object : RvAdapter<NewsListVo>(this, arrayListOf(), R.layout.item_news_list) {
                override fun convert(holder: RVHolder, data: NewsListVo, position: Int) {
                    holder.setText(R.id.item_news_list_title, data.title)
                    holder.setText(R.id.item_news_list_desc, data.digest)
                    val image = holder.getView<ImageView>(R.id.item_news_list_image)
                    val title = holder.getView<TextView>(R.id.item_news_list_title)
                    val content = holder.getView<TextView>(R.id.item_news_list_desc)
                    image.transitionName = "news_image"
                    title.transitionName = "news_title"
//                content.transitionName="news_content"
                    if (!data.imgList.isNullOrEmpty()) {
                        Glide.with(this@NewsListActivity).load(data.imgList[0]).centerCrop()
                            .into(image)
                    }
                    holder.itemView.setOnClickListener {
                        val intent =
                            Intent(this@NewsListActivity, NewsDetailPageActivity::class.java)

                        val pair1 = Pair<View, String>(image , "news_image")

                        val pair2 = Pair<View, String>(title, "news_title")
                        val option = ActivityOptionsCompat.makeSceneTransitionAnimation(
                            this@NewsListActivity,
                            pair1,
//                            pair2  对于文字的变换，涉及到字体颜色和大小，需要自定义，否则表现不好 todo
                        )
                        intent.putExtra("newsId", data.newsId)
                        intent.putExtra("newsImage", data.imgList[0])
                        intent.putExtra("newsTitle", data.title)
                        startActivity(intent, option.toBundle())
                    }
                }

            }
        binding.newsList.adapter = listAdapter

        viewModel.newsLiveData.observe(this) { vos ->
            binding.newsRefresh.finishLoadMore()
            binding.newsRefresh.finishRefresh()
            vos?.let {
                if (page == 1) {
                    listAdapter.replaceAll(it)

                } else {
                    listAdapter.addAll(it)

                }

            }
        }
        viewModel.getNewsList(viewModel.types[0].typeId, page)
    }
}