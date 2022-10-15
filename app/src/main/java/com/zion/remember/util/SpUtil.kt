package com.zion.remember.util

import android.content.Context
import android.content.SharedPreferences
import com.zion.remember.BaseApplication

/*
 *
 */

object SpUtil {
    private const val SHARE_NAME = "share_pref"
    private var sharedPreferences =
        BaseApplication.instance.getSharedPreferences(SHARE_NAME, Context.MODE_PRIVATE)
    private val sharedEditor = sharedPreferences.edit()



    fun getString(key : String) : String{
        return sharedPreferences.getString(key, "").toString()
    }

    fun putString(key: String, value : String) {
        sharedEditor.putString(key, value)
        sharedEditor.commit()
    }

    fun getInt(key : String) : Int{
        return sharedPreferences.getInt(key, 0)
    }

    fun putInt(key: String, value : Int) {
        sharedEditor.putInt(key, value)
        sharedEditor.commit()
    }

    fun getFloat(key : String) : Float{
        return sharedPreferences.getFloat(key, 0f)
    }

    fun putFloat(key: String, value : Float) {
        sharedEditor.putFloat(key, value)
        sharedEditor.commit()
    }

}