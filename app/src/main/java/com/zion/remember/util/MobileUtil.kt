package com.zion.remember.util

import android.os.Build
import android.util.TypedValue
import androidx.fragment.app.FragmentActivity
import com.zion.remember.BaseApplication

object MobileUtil {

    fun getScreenWidth(context: FragmentActivity) : Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            context.windowManager.currentWindowMetrics.bounds.width()
        } else {
            context.windowManager.defaultDisplay.width
        }
    }

    fun getScreenHeight(context: FragmentActivity) : Int{
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            context.windowManager.currentWindowMetrics.bounds.height()
        } else {
            context.windowManager.defaultDisplay.height
        }
    }


    fun sp2px( sp : Float) : Float{
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, BaseApplication.instance.resources.displayMetrics )
    }

    fun dp2px( dp : Float) : Float{
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, BaseApplication.instance.resources.displayMetrics )
    }
}