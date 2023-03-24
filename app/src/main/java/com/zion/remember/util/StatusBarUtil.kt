package com.zion.remember.util

import android.app.Activity
import android.view.View
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

object StatusBarUtil {
    fun getStatusBarHeight() : Int{
//        ViewCompat.setOnApplyWindowInsetsListener(binding.root
//        ) { _, insets ->
//            insets?.getInsets(WindowInsetsCompat.Type.statusBars())?.top
//            insets
//        }
        return 10
    }

    fun setStatusBarBg(activity : Activity, color : Int, isLight : Boolean = false) {
        activity.window.apply {
            clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            statusBarColor =  ContextCompat.getColor(activity, color)
            var ui = decorView.systemUiVisibility

            if (isLight) {
                ui = ui or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            } else {
                ui = ui and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
            }
            decorView.setSystemUiVisibility(ui);

        }
    }



    fun hideStatusBar(view : View){

        ViewCompat.getWindowInsetsController(view)?.apply {

            hide(WindowInsetsCompat.Type.systemBars())
            isAppearanceLightNavigationBars =false
            isAppearanceLightStatusBars = false
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

        }
    }
}