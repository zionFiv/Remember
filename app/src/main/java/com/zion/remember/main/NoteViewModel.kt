package com.zion.remember.main

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.zion.remember.db.AppDatabase
import com.zion.remember.db.NoteInformationVo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class NoteViewModel(application: Application) : AndroidViewModel(application) {
    var startIndex = 0

    fun getNextTen(): LiveData<Result<MutableList<NoteInformationVo>>> {
        return getNoteTen(startIndex + 10)
    }

    //下拉获取更早十条
    fun getLastTen(): LiveData<Result<MutableList<NoteInformationVo>>>? {
        if (startIndex < 0) return null
        return getNoteTen(startIndex - 10)
    }

    //获取第一次十条
    fun getFirstShowTen(): LiveData<Result<MutableList<NoteInformationVo>>> {

        return getNoteTen(0, true)
    }

    private fun getNoteTen(
        start: Int,
        isFirst: Boolean = false
    ): LiveData<Result<MutableList<NoteInformationVo>>> {
        var data = MutableLiveData<Result<MutableList<NoteInformationVo>>>()
        CoroutineScope(Dispatchers.Default).launch {
            Log.d("load", "1 + " + System.currentTimeMillis())
            startIndex = if (isFirst) {
                val count =  getNoteCount()
                if (count > 10) count - 10 else 0
            } else {
                start
            }
            val notes =
                AppDatabase.getInstance(getApplication()).noteDao()
                    .getNoteFrom(startIndex, if (startIndex < 0) startIndex + 10 else 10)//如果初始点小于0，说明需要拉取的数据只有一点
            if (isFirst) {
                val nowDate = SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().time)
                if (notes.isEmpty() || notes[notes.size - 1].noteDate != nowDate) {
                    notes.add(NoteInformationVo(nowDate, "").apply { edit = true })
                }
            }
            Log.d("load", "2 + " + System.currentTimeMillis())
            data.postValue(Result.success(notes))
        }
        return data
    }


    private fun getNoteCount(): Int {
        return AppDatabase.getInstance(getApplication()).noteDao().getNoteCount()

    }


}