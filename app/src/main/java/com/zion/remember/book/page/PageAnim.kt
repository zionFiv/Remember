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


    open fun initAnim(context: Context, w: Int, h: Int) {


    }

    fun setTouchPoint(x: Float, y: Float) {
        mTouchX = x
        mTouchY = y
    }

    abstract fun scrolledPosition() : Point
    abstract fun touchDown(mStartX : Float, mStartY: Float)
    abstract fun touchMove()
    abstract fun touchUp()
    abstract fun isPre() : Boolean
    abstract fun isCancel() : Boolean

    abstract fun draw(canvas: Canvas, curBitmap: Bitmap, nextBitmap: Bitmap)
     fun updateSize(w: Int, h: Int) {
         mViewWidth = w
         mViewHeight = h
    }
}