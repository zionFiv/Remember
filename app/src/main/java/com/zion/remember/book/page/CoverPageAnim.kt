package com.zion.remember.book.page

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Point
import android.graphics.Rect

class CoverPageAnim : HorizontalPageAnim() {

    private var mSrcRect: Rect = Rect()
    private var mDestRect: Rect = Rect()
    private var mNextSrcRect: Rect = Rect()
    private var mNextDestRect: Rect = Rect()
    private var isCancel = false


    override fun updateSize(w: Int, h: Int) {
        super.updateSize(w, h)

    }

    override fun touchDown(x: Float, y: Float) {
        super.touchDown(x, y)
        mSrcRect = Rect(0, 0, mViewWidth, mViewHeight)
        mDestRect = Rect(0, 0, mViewWidth, mViewHeight)
        mNextSrcRect = Rect(0, 0, mViewWidth, mViewHeight)
        mNextDestRect = Rect(0, 0, mViewWidth, mViewHeight)
        lastTouchX = x
    }

    private var lastTouchX = 0f
    private var twoTouchDis = 0f//
    override fun touchMove() {
        if (isPre == null) {
            isPre = mTouchX > mStartX
        }
        twoTouchDis = mTouchX - lastTouchX
        lastTouchX = mTouchX
    }

    override fun touchUp() {
        if (isPre == true) { //
            isCancel = twoTouchDis < 0
        } else {
            isCancel = twoTouchDis > 0
        }
    }

    override fun isCancel(): Boolean {
        return isCancel
    }

    override fun scrolledPosition(): Point {
        var dx = 0
        if (isPre()) {
            var distance = (mTouchX - mStartX).toInt()
            if (distance > mViewWidth) distance = mViewWidth
            if (isCancel) {

                dx = (-distance).toInt()
            } else {
                dx = (mViewWidth - distance).toInt()
            }
        } else {
            if (isCancel) {
                var dis = (mViewWidth - mStartX + mTouchX).toInt()
                if (dis > mViewWidth) {
                    dis = mViewWidth
                }
                dx = mViewWidth - dis
            } else {
                dx = (-(mTouchX + (mViewWidth - mStartX))).toInt()
            }
        }

        return Point(dx, 0)
    }

    override fun draw(canvas: Canvas, curBitmap: Bitmap, nextBitmap: Bitmap) {
        var distance = 0
        if(isPre == true){
            distance = (mTouchX - mStartX).toInt()
            if (distance < 0) distance = 0
            mSrcRect?.right = mViewWidth - distance
            mDestRect?.left = distance
//            mNextSrcRect?.left = mViewWidth -  distance
//            mNextDestRect?.right =  distance
            canvas.drawBitmap(nextBitmap, mNextSrcRect, mNextDestRect, null)
            canvas.drawBitmap(curBitmap, mSrcRect, mDestRect, null)
        } else {
            distance = (mViewWidth - mStartX + mTouchX).toInt()
            if (distance > mViewWidth) distance = mViewWidth
            mSrcRect?.left = mViewWidth - distance
            mDestRect?.right = distance
//            mNextSrcRect?.right = mViewWidth - distance
//            mNextDestRect?.left = distance
            canvas.drawBitmap(nextBitmap, mNextSrcRect, mNextDestRect, null)
            canvas.drawBitmap(curBitmap, mSrcRect, mDestRect, null)


        }
    }
}