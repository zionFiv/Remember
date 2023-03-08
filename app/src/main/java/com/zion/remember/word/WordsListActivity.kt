package com.zion.remember.word

import android.graphics.Rect
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.zion.remember.R
import com.zion.remember.adapter.RVHolder
import com.zion.remember.adapter.RvAdapter
import com.zion.remember.databinding.ActivityWordsListBinding
import com.zion.remember.db.WordsVo
import com.zion.remember.util.MobileUtil

/*
 * ActivityWordsListBinding原理
 * Room的使用及原理
 * recyclerview adapter轮子
 * 下拉上拉刷新
 */

class WordsListActivity : AppCompatActivity() {
    private var page = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityWordsListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val model = ViewModelProvider(this).get(WordsListViewModel::class.java)

        binding.wordsList.layoutManager = LinearLayoutManager(this)
        val adapter =
            object : RvAdapter<WordsVo>(this, arrayListOf(), R.layout.item_words) {
                override fun convert(holder: RVHolder, data: WordsVo, position: Int) {
                    holder.setText(R.id.word_eng, data.word)
                    holder.setText(R.id.word_exp, data.wordExplain)
                }
            }

        binding.wordsList.adapter = adapter
        binding.wordsList.addItemDecoration(object  : RecyclerView.ItemDecoration(){
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

        binding.wordAdd.setOnClickListener {

            WordsAddDialogFragment().show(supportFragmentManager, "add_word")
        }

        binding.wordsRefresh.setEnableRefresh(false)
        binding.wordsRefresh.setOnLoadMoreListener {
            page++
            model.getWords(page).observe(this@WordsListActivity){
                adapter.addAll(it)

            }
            it.finishLoadMore()

        }

        model.getWords(page).observe(this@WordsListActivity){
            adapter.replaceAll(it)

        }


    }


}