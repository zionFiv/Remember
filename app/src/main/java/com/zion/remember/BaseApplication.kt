package com.zion.remember

import android.app.Application
import android.content.Context
import android.util.Log
import com.dianping.logan.Logan
import com.zion.remember.util.LogUtil

class BaseApplication : Application() {
    companion object{
        lateinit var instance : Application
    }

    override fun attachBaseContext(base: Context?) {
        Log.i("BookFragment", "attachbase")
        super.attachBaseContext(base)
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        LogUtil.init(this)
        Logan.w("this is log", 0x12)
    }



}