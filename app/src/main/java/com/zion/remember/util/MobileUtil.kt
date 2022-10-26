package com.zion.remember.util

import android.content.Context
import android.os.Build
import android.util.TypedValue
import android.view.View
import android.view.inputmethod.InputMethodManager
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

    fun hideSoftInput(view : View){
       val imm = BaseApplication.instance.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}