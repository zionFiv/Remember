package com.zion.remember.joke

import android.graphics.Rect
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.zion.remember.R
import com.zion.remember.adapter.RVHolder
import com.zion.remember.adapter.RvAdapter
import com.zion.remember.databinding.ActivityJokeListBinding
import com.zion.remember.http.BaseData
import com.zion.remember.http.ForumData
import com.zion.remember.http.ForumList
import com.zion.remember.http.RManager
import com.zion.remember.util.MobileUtil
import com.zion.remember.util.SpUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class JokeListActivity : AppCompatActivity() {
    private var page = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityJokeListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.jokeList.layoutManager = LinearLayoutManager(this)
        binding.jokeList.addItemDecoration(object : RecyclerView.ItemDecoration(){
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
        val jokeAdapter = object : RvAdapter<ForumList>(this, arrayListOf(), R.layout.item_joke){
            override fun convert(holder: RVHolder, data: ForumList, position: Int) {
                holder.setText(R.id.item_joke_text, data.content)
            }

        }
        binding.jokeList.adapter = jokeAdapter
        binding.jokeRefresh.setOnLoadMoreListener {
            binding.jokeRefresh.finishLoadMore()
            page++
            getJoke(page).observe(this){
                jokeAdapter.addAll(it.list.toMutableList())
            }

        }

        binding.jokeRefresh.setOnRefreshListener {
            binding.jokeRefresh.finishRefresh()
            page = 1
            getJoke(page).observe(this){
                jokeAdapter.replaceAll(it.list.toMutableList())
            }
        }

        getJoke(page).observe(this){
            jokeAdapter.replaceAll(it.list.toMutableList())
            SpUtil.putString("joke_sp", Gson().toJson(it))
        }
    }

    fun getJoke(page : Int) : MutableLiveData<ForumData> {
        var data = MutableLiveData<ForumData>()

        CoroutineScope(Dispatchers.Default).launch {
            RManager.getForumList(page, object : Callback<BaseData<ForumData>> {
                override fun onResponse(call: Call<BaseData<ForumData>>, response: Response<BaseData<ForumData>>) {
                    response.body()?.data?.let {
                        data.postValue(it)

                    }
                }

                override fun onFailure(call: Call<BaseData<ForumData>>, t: Throwable) {
                }

            })
        }
        return data
    }

}