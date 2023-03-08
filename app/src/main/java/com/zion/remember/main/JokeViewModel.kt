package com.zion.remember.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zion.remember.db.AppDatabase
import com.zion.remember.db.WordsVo
import com.zion.remember.http.BaseData
import com.zion.remember.http.ForumData
import com.zion.remember.http.RManager
import com.zion.remember.http.WeatherVo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class JokeViewModel : ViewModel() {
    fun getJoke() : MutableLiveData<ForumData>{
        var data = MutableLiveData<ForumData>()

        viewModelScope.launch {
            RManager.getForumList( object : Callback<BaseData<ForumData>> {
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


    fun getCityWeather(cityName :String) : MutableLiveData<WeatherVo>{
        var data = MutableLiveData<WeatherVo>()

        viewModelScope.launch {
            RManager.getCityWeather( cityName , object : Callback<BaseData<WeatherVo>> {
                override fun onResponse(call: Call<BaseData<WeatherVo>>, response: Response<BaseData<WeatherVo>>) {
                    response.body()?.data?.let {
                        data.postValue(it)

                    }
                }

                override fun onFailure(call: Call<BaseData<WeatherVo>>, t: Throwable) {
                }

            })
        }
        return data
    }

    fun getWords() : LiveData<MutableList<WordsVo>>{
        val data = MutableLiveData<MutableList<WordsVo>>()
        CoroutineScope(Dispatchers.Default).launch {
            val vo = AppDatabase.getInstance().wordsDao().getWords(0, 10)
            data.postValue(vo)
        }
        return data

    }

}