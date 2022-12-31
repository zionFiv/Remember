package com.zion.remember.book.page

import android.content.Context
import android.graphics.*
import android.os.Handler
import android.os.Looper
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.BackgroundColorSpan
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.view.animation.LinearInterpolator
import android.widget.Scroller
import androidx.core.content.ContextCompat
import com.zion.remember.R
import com.zion.remember.util.LogUtil
import com.zion.remember.util.MobileUtil
import com.zion.remember.vo.ChapterPageVo
import java.lang.StringBuilder
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

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
    private var mHorizontalEdge = HorizontalMargin.Big
    private var mLineEdge = LineMargin.Small
    private var mContent: String = ""
    private var contentSize: Float = 0.0f//
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

    fun setPageMode(mode: AnimMode) {
        animMode = when (mode) {
            AnimMode.Simulate ->
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
            if (!isMove || isLongClick) { // 静止显示
                canvas.drawBitmap(this, 0.0f, 0.0f, null)
            } else {
                Log.i("pageView", "compute onDraw $isMove? $currentBitmap")
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
    var isLongClick = false

    override fun onTouchEvent(event: MotionEvent): Boolean {
//        super.onTouchEvent(event)
        animMode.setTouchPoint(event.x, event.y)

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                mStartX = event.x
                mStartY = event.y
                isMove = false
                moveLength = 0f
                isLongClick = false
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
                Handler(Looper.getMainLooper()).postDelayed({
                    if (!isMove
                        && mStartX > mHorizontalEdge.space && mStartX < mPageWidth - mHorizontalEdge.space
                    ) {
                        isLongClick = true
                    }
                }, 1000)
                Log.i("pageView", "compute ACTION_DOWN $isMove")
                animMode.touchDown(mStartX, mStartY)
            }
            MotionEvent.ACTION_MOVE -> {
                val slop = ViewConfiguration.get(context).scaledTouchSlop
                moveLength += abs(mStartX - event.x)
                isMove = moveLength > slop
                Log.i("pageView", "compute ACTION_MOVE $isMove $isLongClick")
                if (isLongClick) {
                    isMove = false
                    currentBitmap = currentChapter?.let {
                        var left = mStartX
                        var right = event.x

                        if (mStartX < mHorizontalEdge.space) left = mHorizontalEdge.space
                        if (mStartX > mPageWidth - mHorizontalEdge.space) left =
                            mPageWidth - mHorizontalEdge.space
                        var leftCount = ((left - mHorizontalEdge.space) / mFontSize).toInt()
                        if (event.y < mStartY) leftCount++
                        left = mHorizontalEdge.space + leftCount * mFontSize
                        var rightCount = ((right - mHorizontalEdge.space) / mFontSize).toInt()
                        if (event.y > mStartY) rightCount++
                        right = mHorizontalEdge.space + rightCount * mFontSize
                        getBitmap(
                            it,
                            RectF(leftCount.toFloat(), mStartY, rightCount.toFloat(), event.y)
                        )
                    }
                    invalidate()
                } else if (isMove) {
                    animMode.touchMove()
                    invalidate()
                }
            }
            MotionEvent.ACTION_UP -> {
                Log.i("PageView", "compute ACTION_UP $isMove")
                if (isLongClick) {
                    isLongClick = false
                    mTouchListener?.longTouch(copySb.toString(), mStartY, event.y)
                } else if (isMove) {
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

    var currentChapter: ChapterPageVo? = null
    fun setBitmap(chapters: List<ChapterPageVo>) {
        if (chapters[0].contents.isNotEmpty()) {
            befBitmap = getBitmap(chapters[0])
        }

        currentBitmap = getBitmap(chapters[1])
        currentChapter = chapters[1]
        if (chapters[2].contents.isNotEmpty()) {
            nextBitMap = getBitmap(chapters[2])
        }
        invalidate()

    }

    var copySb = StringBuilder()
    private fun getBitmap(chapterPageVo: ChapterPageVo, rectF: RectF? = null): Bitmap {
        titleStr = chapterPageVo.title
        val contents = mutableListOf<String>()
        contents.clear()
        contents.addAll(chapterPageVo.contents)
        copySb.clear()
        var yPosition = Edge.topMargin//起始高度
        val tempBitmap = Bitmap.createBitmap(mPageWidth, mPageHeight, Bitmap.Config.RGB_565)
        tempBitmap?.let { bitmap ->
            val canvas = Canvas(bitmap)
            canvas.drawColor(ContextCompat.getColor(context, bgColor.color))
            canvas.drawText(titleStr, mHorizontalEdge.space, yPosition, tipPaint)
            yPosition += MobileUtil.dp2px(10f)
            contents.forEach {
                yPosition += (mFontSize)
                if (isLongClick) {//长按高亮
                    if(it.isNullOrBlank()) return@forEach
                    var buidler = SpannableStringBuilder(it)
                    buidler.setSpan(
                        BackgroundColorSpan(
                            ContextCompat.getColor(
                                context,
                                R.color.color_08ef
                            )
                        ), 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                    textPaint.color = buidler.getSpans<BackgroundColorSpan>(
                        0,
                        1,
                        BackgroundColorSpan::class.java
                    )[0].backgroundColor

                    rectF?.let { it1 ->
                        var startX = it1.left * mFontSize + mHorizontalEdge.space
                        var endX = it1.right * mFontSize + mHorizontalEdge.space
                        if (yPosition in rectF.bottom - mLineEdge.space..rectF.top + mLineEdge.space || yPosition in rectF.top - mLineEdge.space..rectF.bottom + mLineEdge.space) {
                            if (abs(rectF.bottom - rectF.top) < mFontSize) {//只有一行

                                if (it.length < min(it1.left, it1.right)) {

                                } else if (it.length < max(it1.left, it1.right)) {
                                    canvas.drawRect(
                                        RectF(
                                            min(it1.left, it1.right) * mFontSize + mHorizontalEdge.space,
                                            yPosition - mFontSize,
                                            it.length * mFontSize + mHorizontalEdge.space,
                                            yPosition + MobileUtil.dp2px(2f)
                                        ), textPaint
                                    )
                                    copySb.append(
                                        it.substring(
                                            min(it1.left, it1.right).toInt(),
                                            it.length
                                        )
                                    )
                                } else {
                                    canvas.drawRect(
                                        RectF(
                                            startX,
                                            yPosition - mFontSize,
                                            endX,
                                            yPosition + MobileUtil.dp2px(2f)
                                        ), textPaint
                                    )
                                    copySb.append(
                                        it.substring(
                                            min(it1.left, it1.right).toInt(),
                                            max(it1.left, it1.right).toInt()
                                        )
                                    )
                                }

                            } else {
                                if (rectF.top < rectF.bottom) {//从上往下
                                    if (yPosition < rectF.top + mFontSize) {//第一行
                                        LogUtil.d("position 上下1 $yPosition top ${rectF.top}  bottom ${rectF.bottom} $mFontSize")
                                        if(it1.left < it.length){
                                            canvas.drawRect(
                                                RectF(
                                                    startX,
                                                    yPosition - mFontSize,
                                                    it.length * mFontSize + mHorizontalEdge.space,
                                                    yPosition + MobileUtil.dp2px(2f)
                                                ), textPaint
                                            )
                                            copySb.append(it.substring(it1.left.toInt()))
                                        }

                                    } else if (yPosition > rectF.bottom - mFontSize) {//最后一行
                                        LogUtil.d("position 上下末 $yPosition top ${rectF.top}  bottom ${rectF.bottom} $mFontSize")
                                        if(it1.right < it.length) {
                                            canvas.drawRect(
                                                RectF(
                                                    mHorizontalEdge.space,
                                                    yPosition - mFontSize,
                                                    endX,
                                                    yPosition + MobileUtil.dp2px(2f)
                                                ), textPaint
                                            )
                                            copySb.append(it.substring(0, it1.right.toInt()))
                                        } else {
                                            canvas.drawRect(
                                                RectF(
                                                    mHorizontalEdge.space,
                                                    yPosition - mFontSize,
                                                    it.length * mFontSize + mHorizontalEdge.space,
                                                    yPosition + MobileUtil.dp2px(2f)
                                                ), textPaint
                                            )
                                            copySb.append(it)
                                        }
                                    } else {
                                        LogUtil.d("position 上下中 $yPosition top ${rectF.top}  bottom ${rectF.bottom} $mFontSize")

                                        canvas.drawRect(
                                            RectF(
                                                mHorizontalEdge.space,
                                                yPosition - mFontSize,
                                                it.length * mFontSize + mHorizontalEdge.space,
                                                yPosition + MobileUtil.dp2px(2f)
                                            ), textPaint
                                        )
                                        copySb.append(it)
                                    }
                                } else {//从下往上
                                    if (yPosition < rectF.bottom + mFontSize) {//第一行
                                        LogUtil.d("position 下上1 $yPosition top ${rectF.top}  bottom ${rectF.bottom} $mFontSize")
                                        if(it1.right < it.length) {
                                            canvas.drawRect(
                                                RectF(
                                                    endX,
                                                    yPosition - mFontSize,
                                                    it.length * mFontSize + mHorizontalEdge.space,
                                                    yPosition + MobileUtil.dp2px(2f)
                                                ), textPaint
                                            )
                                            copySb.append(it1.right.toInt())
                                        }
                                    } else if (yPosition > rectF.top - mFontSize) {//最后一行
                                        LogUtil.d("position 下上末 $yPosition top ${rectF.top}  bottom ${rectF.bottom} $mFontSize")
                                        if(it1.left < it.length) {
                                            canvas.drawRect(
                                                RectF(
                                                    mHorizontalEdge.space,
                                                    yPosition - mFontSize,
                                                    startX,
                                                    yPosition + MobileUtil.dp2px(2f)
                                                ), textPaint
                                            )
                                            copySb.append(0, it1.left.toInt())
                                        } else {
                                            canvas.drawRect(
                                                RectF(
                                                    mHorizontalEdge.space,
                                                    yPosition - mFontSize,
                                                    it.length * mFontSize + mHorizontalEdge.space,
                                                    yPosition + MobileUtil.dp2px(2f)
                                                ), textPaint
                                            )
                                            copySb.append(0)
                                        }
                                    } else {
                                        LogUtil.d("position 下上中 $yPosition top ${rectF.top}  bottom ${rectF.bottom} $mFontSize")

                                        canvas.drawRect(
                                            RectF(
                                                mHorizontalEdge.space,
                                                yPosition - mFontSize,
                                                it.length*mFontSize + mHorizontalEdge.space,
                                                yPosition + MobileUtil.dp2px(2f)
                                            ), textPaint
                                        )
                                        copySb.append(it)
                                    }
                                }
                            }
                        }

                    }
                }
                textPaint.color = ContextCompat.getColor(context, R.color.color_666666)
                canvas.drawText(it, mHorizontalEdge.space, yPosition, textPaint)

                yPosition += mLineEdge.space.toInt()//行间距
            }
            canvas.drawText(
                "${chapterPageVo.position}",
                mHorizontalEdge.space,
                mPageHeight.toFloat() - MobileUtil.dp2px(20.0f),
                tipPaint
            )

            LogUtil.d("position ${copySb.toString()}")

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

    fun changeHorizontalEdge(horizontalEdge: HorizontalMargin) {
        mHorizontalEdge = horizontalEdge
    }

    fun changeLineEdge(lineEdge: LineMargin) {
        mLineEdge = lineEdge
    }

    interface TouchListener {
        fun onTouch(): Boolean
        fun center()
        fun prePage()
        fun nextPage()
        fun cancel()
        fun longTouch(param : String, positionStart : Float, positionEnd: Float)
    }
}