package com.zion.remember.word

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.zion.remember.db.AppDatabase
import com.zion.remember.db.WordsVo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/*
 * LiveData
 * 协程使用
 *      scope:
 */

class WordsListViewModel(application: Application) : AndroidViewModel(application) {

    fun getWords(page:Int) : LiveData<MutableList<WordsVo>> {
        var data = MutableLiveData<MutableList<WordsVo>>()

        CoroutineScope(Dispatchers.Default).launch {
            val vo = AppDatabase.getInstance(getApplication()).wordsDao().getWords(page, 10)
            data.postValue(vo)
        }

        return  data
    }

    fun saveWord(vo : WordsVo) {
        CoroutineScope(Dispatchers.Default).launch {
            //如何确认save成功
            AppDatabase.getInstance(getApplication()).wordsDao().saveWord(vo)

        }

    }

}