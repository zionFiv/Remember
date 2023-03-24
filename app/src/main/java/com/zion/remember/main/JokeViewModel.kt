package com.zion.remember.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.zion.remember.db.AppDatabase
import com.zion.remember.db.WordsVo
import com.zion.remember.http.BaseData
import com.zion.remember.http.ForumData
import com.zion.remember.http.RManager
import com.zion.remember.http.WeatherVo
import com.zion.remember.util.SpUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class JokeViewModel : ViewModel() {
    private val WEATHER_SP = "weather_sp"
    private val JOKE_SP = "joke_sp"
    fun getJoke() : MutableLiveData<ForumData>{
        var data = MutableLiveData<ForumData>()

        viewModelScope.launch {
           val historyJoke = SpUtil.getString(JOKE_SP)
            if (historyJoke.isBlank()) {
                RManager.getForumList(1, object : Callback<BaseData<ForumData>> {
                    override fun onResponse(
                        call: Call<BaseData<ForumData>>,
                        response: Response<BaseData<ForumData>>
                    ) {
                        response.body()?.data?.let {
                            data.postValue(it)
                            SpUtil.putString(JOKE_SP, Gson().toJson(it))
                        }
                    }

                    override fun onFailure(call: Call<BaseData<ForumData>>, t: Throwable) {
                    }

                })
            } else {
                val vo = Gson().fromJson(historyJoke, ForumData::class.java)
                data.postValue(vo)
            }
        }
        return data
    }


    fun getCityWeather(cityName :String) : MutableLiveData<WeatherVo>{
        var data = MutableLiveData<WeatherVo>()

        viewModelScope.launch {
            val historyWeather = SpUtil.getString(WEATHER_SP)
            if(historyWeather.isNotBlank()) {
                val vo = Gson().fromJson(historyWeather, WeatherVo::class.java)
                val currentDate = SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().time)
                if (vo.reportTime.contains(currentDate)) {
                    data.postValue(vo)
                    return@launch
                }
            }

            RManager.getCityWeather( cityName , object : Callback<BaseData<WeatherVo>> {
                override fun onResponse(call: Call<BaseData<WeatherVo>>, response: Response<BaseData<WeatherVo>>) {
                    response.body()?.data?.let {
                        data.postValue(it)
                        SpUtil.putString(WEATHER_SP, Gson().toJson(it))

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