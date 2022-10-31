package com.zion.remember.book.page

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Point

abstract class HorizontalPageAnim : PageAnim() {
    var isPre: Boolean? = null
    var mStartX = 0f
    var mStartY = 0f

    override fun initAnim(context: Context, w: Int, h: Int) {
        mViewWidth = w
        mViewHeight = h
    }

    override fun touchDown(x: Float, y: Float){
        mStartX = x
        mStartY = y
        isPre = null
    }

    override fun isPre(): Boolean {
        return isPre ?: false
    }
}