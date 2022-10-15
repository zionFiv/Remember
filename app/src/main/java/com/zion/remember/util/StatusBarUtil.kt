package com.zion.remember.util

import android.util.Log
import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
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

    fun setStatusBarBg() {

    }

    fun hideStatusBar(view : View){

        ViewCompat.getWindowInsetsController(view)?.apply{
            hide(WindowInsetsCompat.Type.systemBars())
            isAppearanceLightNavigationBars =false
            isAppearanceLightStatusBars = false
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

        }
    }
}