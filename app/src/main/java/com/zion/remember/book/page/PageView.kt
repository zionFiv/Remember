package com.zion.remember.book.page

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.view.animation.LinearInterpolator
import android.widget.Scroller
import androidx.core.content.ContextCompat
import com.zion.remember.R
import com.zion.remember.util.MobileUtil
import com.zion.remember.vo.ChapterPageVo
import kotlin.math.abs

/*
不能快翻 （已处理）
偶尔折页（或者点击中间，旁边会先折起闪一下） (已处理）
 * 存在的问题：偶尔白屏，，
 */

class PageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) :
    View(context, attrs, defStyleAttr) {
    private var mFontSize = MobileUtil.sp2px(16f)
    private var textPaint: Paint = Paint()
    private var tipPaint: Paint = Paint()
    private var statusBarHeight = 0
    private var mContent: String = ""
    private var titleSize: Float = 0.0f //
    private var contentSize: Float = 0.0f//
    private var marginWidth: Float = 0.0f//
    var mPageWidth: Int = 0 //
    var mPageHeight: Int = 0 //
    private var titleStr: String = ""//

    private var currentBitmap: Bitmap? = null
    private var befBitmap: Bitmap? = null
    private var nextBitMap: Bitmap? = null
    private var animMode: PageAnim = SimulatePageAnim()
    private var isMove = false
    private var bgColor = BgColor.Bg1
    var mScroller: Scroller

    init {
        textPaint = Paint().apply {
            color = ContextCompat.getColor(context, R.color.color_666666)
            textSize = mFontSize
        }
        tipPaint = Paint().apply {
            color = ContextCompat.getColor(context, R.color.color_999999)
            textSize = MobileUtil.sp2px(12f)
        }
        mScroller = Scroller(context, LinearInterpolator())

    }

    fun setPageMode(mode : AnimMode) {
        animMode = when(mode) {
            AnimMode.Simuluate ->
                SimulatePageAnim()
            AnimMode.Slide ->
                SlidePageAnim()
            AnimMode.Cover ->
                CoverPageAnim()
        }
        animMode.initAnim(context, mPageWidth, mPageHeight)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        Log.i("pageView", "currentBitmap is null? $currentBitmap")
        currentBitmap?.apply {
            if (!isMove) { // 静止显示
                canvas.drawBitmap(this, 0.0f, 0.0f, null)
            } else {
                // 滑动静止
                if (animMode.isPre()) {
                    animMode.draw(canvas, this, befBitmap ?: this)
                } else {
                    animMode.draw(canvas, this, nextBitMap ?: this)
                }
            }
        }

    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mPageWidth = w
        mPageHeight = h
        animMode.updateSize(w, h)
    }


    var mStartX = 0f
    var mStartY = 0f
    var moveLength = 0f

    override fun onTouchEvent(event: MotionEvent): Boolean {
//        super.onTouchEvent(event)
        animMode.setTouchPoint(event.x, event.y)

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                mStartX = event.x
                mStartY = event.y
                isMove = false
                moveLength = 0f
                if (!mScroller.isFinished) {//
                    mScroller.abortAnimation()
                    when {
                        animMode.isCancel() -> {
                            mTouchListener?.cancel()
                        }
                        animMode.isPre() -> {
                            currentBitmap = befBitmap
                            mTouchListener?.prePage()
                        }
                        else -> {
                            currentBitmap = nextBitMap
                            mTouchListener?.nextPage()
                        }
                    }
                }
                Log.i("pageView", "compute ACTION_DOWN $isMove")
                animMode.touchDown(mStartX, mStartY)
            }
            MotionEvent.ACTION_MOVE -> {
                val slop = ViewConfiguration.get(context).scaledTouchSlop
                moveLength += abs(mStartX - event.x)
                isMove = moveLength > slop
                Log.i("pageView", "compute ACTION_MOVE $isMove")
                if (isMove) {
                    animMode.touchMove()
                    invalidate()
                }
            }
            MotionEvent.ACTION_UP -> {
                Log.i("PageView", "wrong page -- touchup $isMove")

                if (isMove) {
                    animMode.touchUp()
                    val p = animMode.scrolledPosition()
                    mScroller.startScroll(event.x.toInt(), event.y.toInt(), p.x, p.y, 400)
                    invalidate()
                } else {

                    val mCenterRect = RectF(
                        (mPageWidth / 5).toFloat(),
                        (mPageHeight / 3).toFloat(),
                        (mPageWidth * 4 / 5).toFloat(),
                        (mPageHeight * 2 / 3).toFloat()
                    )
                    if (mCenterRect.contains(event.x, event.y)) {
                        mTouchListener?.center()
                    } else {
                        mTouchListener?.onTouch()
                    }

                }
            }


        }

        return true
    }

    override fun computeScroll() {
        if (mScroller.computeScrollOffset()) {
            val x = mScroller.currX
            val y = mScroller.currY
            Log.i("pageView", "compute scroll $x, $y ${mScroller.finalX} ${mScroller.finalY}")
            animMode.setTouchPoint(x.toFloat(), y.toFloat())
            animMode.touchMove()
            if (mScroller.finalX === x && mScroller.finalY === y) {//结束后的动作
                isMove = false
                when {
                    animMode.isCancel() -> {
                        mTouchListener?.cancel()
                    }
                    animMode.isPre() -> {
                        currentBitmap = befBitmap
                        mTouchListener?.prePage()
                    }
                    else -> {
                        currentBitmap = nextBitMap
                        mTouchListener?.nextPage()
                    }
                }

            } else {

                invalidate()
            }
        }
        super.computeScroll()

    }

    override fun performClick(): Boolean {
        Log.i("pageview", "performclick")
        return super.performClick()
    }

    fun setBitmap(chapters: List<ChapterPageVo>) {
        if (chapters[0].contents.isNotEmpty()) {
            befBitmap = getBitmap(chapters[0])
        }

        currentBitmap = getBitmap(chapters[1])
        if (chapters[2].contents.isNotEmpty()) {
            nextBitMap = getBitmap(chapters[2])
        }
        invalidate()

    }

    private fun getBitmap(chapterPageVo: ChapterPageVo): Bitmap {
        titleStr = chapterPageVo.title
        val contents = mutableListOf<String>()
        contents.clear()
        contents.addAll(chapterPageVo.contents)
        var yPosition = MobileUtil.dp2px(20f) + MobileUtil.sp2px(12f)
        val tempBitmap = Bitmap.createBitmap(mPageWidth, mPageHeight, Bitmap.Config.RGB_565)
        tempBitmap?.let { bitmap ->
            val canvas = Canvas(bitmap)
            canvas.drawColor(ContextCompat.getColor(context, bgColor.color))
            canvas.drawText(titleStr, MobileUtil.dp2px(15.0f), yPosition, tipPaint)
            yPosition += MobileUtil.dp2px(10f)
            contents.forEach {
                Log.i("pageView", "contents : $it")
                yPosition += (MobileUtil.sp2px(1f) + mFontSize)
                canvas.drawText(it, MobileUtil.dp2px(15.0f), yPosition, textPaint)
                yPosition += ((MobileUtil.sp2px(1f) + mFontSize) / 2).toInt()
            }
            canvas.drawText(
                "${chapterPageVo.position}",
                MobileUtil.dp2px(15.0f),
                mPageHeight.toFloat() - MobileUtil.dp2px(15.0f),
                tipPaint
            )

        }
        return tempBitmap
    }

    private var mTouchListener: TouchListener? = null
    fun setTouchListener(mTouchListener: TouchListener?) {
        this.mTouchListener = mTouchListener
    }

    fun setFontSize(fontSize: Float) {
        mFontSize = fontSize
        textPaint.textSize = mFontSize
    }

    fun changeBgColor(bg: BgColor) {
        bgColor = bg

    }

    interface TouchListener {
        fun onTouch(): Boolean
        fun center()
        fun prePage()
        fun nextPage()
        fun cancel()
    }
}