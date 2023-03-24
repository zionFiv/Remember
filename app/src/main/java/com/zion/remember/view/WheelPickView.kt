package com.zion.remember.view

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.Scroller
import com.zion.remember.util.LogUtil
import com.zion.remember.util.MobileUtil
import java.lang.Math.abs

/*
 * 滚动选择view
 * 1. 绘制text
 * 2.添加touch事件
 * 3.添加scorller
 * 4.添加camera matrix
 */

class WheelPickView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private val mPaint = Paint()
    private val textSize = MobileUtil.sp2px(16.0f)
    private var mTouchY = 0 //当前触摸点
    private var mScroller: Scroller
    private var mTracker: VelocityTracker? = null
    private var mPath = Path()
    private var mViewWidth: Int = 0
    private var mViewHeight: Int = 0
    private var mFirstItemPositionY = 0 // 第一个item Y轴起始位置
    private var isTouching = true
    private var itemHeight = 0f
    private var mData = mutableListOf<String>()
    private var mCamera = Camera()
    private var mMatrix = Matrix()
    private var mZMatrix = Matrix()
    private var mRadius = 0f
    private var mCircleLength = 0f
    private var mRect = Rect()//计算每一个item的长度
    private var mTotalDisplayLength = 0f //轮盘显示的总长度
    private var currentIndex = 0;

    init {
        mPaint.textSize = textSize
//        mPaint.style = Paint.Style.STROKE

        itemHeight = textSize + MobileUtil.dp2px(25f)
        mScroller = Scroller(context, LinearInterpolator())
        mTotalDisplayLength = itemHeight * 8
    }

    fun setDatas(items : MutableList<String>){
        mData.clear()
        mData.addAll(items)
        invalidate()
    }


    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mViewWidth = w
        mViewHeight = h
        mFirstItemPositionY = mViewHeight / 2
        mRadius = itemHeight * 8 / 3.14f
        mCircleLength = mRadius * 3.14f
        LogUtil.d("WheelPickView mViewHeight ${mViewHeight}  $height")
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        mData.forEachIndexed { index, str ->
            mPaint.getTextBounds(str, 0, str.length, mRect)
            LogUtil.d("WheelPickView $index mRect ${mRect.width()} ${str.length * textSize}")
            var centerX = (mViewWidth - mRect.width()) / 2f
            var centerY = mFirstItemPositionY + index * itemHeight
            if(!isTouching && centerY - mViewHeight / 2 < 1) {
                currentIndex = index
            }
            mMatrix.reset()
            mCamera.save()
            canvas?.save()
            //150代表总的显示弧度
            var degree = 150 * (centerY - mViewHeight / 2) / mTotalDisplayLength
            if (abs(degree) > 90) return@forEachIndexed
            LogUtil.d("WheelPickView $index degree $degree")
            mCamera.rotateX(-degree)

            mCamera.getMatrix(mMatrix)
            mCamera.restore()

            mMatrix.preTranslate(-centerX, -centerY)
            mMatrix.postTranslate(centerX, centerY)
            canvas?.concat(mMatrix)
            mZMatrix.reset()
            mCamera.save()
            LogUtil.d("WheelPickView scale ${centerY - mViewHeight / 2}")
//            mCamera.translate(0f,0f,abs(centerY - mViewHeight / 2))
            mCamera.getMatrix(mZMatrix)
            var scale = 1.0- abs(centerY - mViewHeight / 2) / (itemHeight * 8)
            LogUtil.d("WheelPickView scale ${scale}")
            mZMatrix.setScale(scale.toFloat(), 1.0f)

            mCamera.restore()
            mZMatrix.preTranslate(-centerX, -centerY)
            mZMatrix.postTranslate(centerX + (mRect.width() * (1 - scale) / 2).toFloat() , centerY)

            canvas?.concat(mZMatrix)
            mPaint.alpha = (255 * scale).toInt()
            canvas?.drawText(
                str,
                centerX,
                centerY,
                mPaint
            )

            canvas?.restore()
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (mTracker == null) {
            mTracker = VelocityTracker.obtain()
        }
        mTracker?.addMovement(event)
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                isTouching = true
                if (!mScroller.isFinished) {
                    mScroller.abortAnimation()
                }
                mTouchY = event.y.toInt()
            }
            MotionEvent.ACTION_MOVE -> {
                var distance = event.y - mTouchY
                mFirstItemPositionY += distance.toInt()
                mTouchY = event.y.toInt()
                invalidate()
            }
            MotionEvent.ACTION_UP -> {
                mTracker?.computeCurrentVelocity(600)
                mTracker?.yVelocity?.let {
                    if (abs(it) > 0.1) {
                        mTouchY = event.y.toInt()
                        mScroller.fling(
                            event.x.toInt(), event.y.toInt(),
                            mTracker?.xVelocity?.toInt() ?: 0, it.toInt(),
                            0, width, 0, height
                        )
                        invalidate()

                    }
                }

                if (mTracker != null) {
                    mTracker?.recycle()
                    mTracker = null
                }
                isTouching = false
            }


        }
        return true

    }

    override fun computeScroll() {

        if (mScroller.computeScrollOffset()) {

            mFirstItemPositionY += (mScroller.currY - mTouchY)

            mTouchY = mScroller.currY
            if (mFirstItemPositionY > mViewHeight / 2) mFirstItemPositionY = mViewHeight / 2

            invalidate()
        } else if (!isTouching) {

            var dis = abs(mFirstItemPositionY - mViewHeight / 2) % itemHeight
            if (dis > 0.1f) {
                mTouchY = mFirstItemPositionY
                if (dis < itemHeight / 2) {
                    mScroller.startScroll(0, mFirstItemPositionY, 0, (dis).toInt())
                    invalidate()
                } else {
                    mScroller.startScroll(0, mFirstItemPositionY, 0, (-(itemHeight - dis)).toInt())
                    invalidate()
                }
            }

        }
        super.computeScroll()
    }

}