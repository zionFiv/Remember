package com.zion.remember.book.page

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Point

abstract class PageAnim {
    var mTouchX: Float = 0f
    var mTouchY: Float = 0f
    var mViewWidth: Int = 0
    var mViewHeight: Int = 0

    fun setTouchPoint(x: Float, y: Float) {
        mTouchX = x
        mTouchY = y
    }

    open fun updateSize(w: Int, h: Int) {
        mViewWidth = w
        mViewHeight = h
    }

    abstract fun initAnim(context: Context, w: Int, h: Int)
    abstract fun scrolledPosition(): Point // 计算最终需要滑向的位置
    abstract fun isPre() : Boolean
    abstract fun touchDown(x: Float, y: Float)
    abstract fun touchMove()
    abstract fun touchUp()
    abstract fun isCancel(): Boolean
    abstract fun draw(canvas: Canvas, curBitmap: Bitmap, nextBitmap: Bitmap)


}