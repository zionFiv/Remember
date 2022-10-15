package com.zion.remember.book.page

import android.content.Context
import android.graphics.*
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.util.Log
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.hypot

class SimulatePageAnim : PageAnim() {
    var mMiddleX: Float = 0f
    var mMiddleY: Float = 0f
    var mCornerX: Int = 0
    var mCornerY: Int = 0
    var mIsRTandLB = false
    var mBezierStart1 = PointF() // 贝塞尔曲线起始点
    var mBezierControl1 = PointF()// 贝塞尔曲线控制点
    var mBezierVertex1 = PointF()// 贝塞尔曲线顶点
    var mBezierEnd1 = PointF()// 贝塞尔曲线结束点

    var mBezierStart2 = PointF() // 贝塞尔曲线起始点
    var mBezierControl2 = PointF()// 贝塞尔曲线控制点
    var mBezierVertex2 = PointF()// 贝塞尔曲线顶点
    var mBezierEnd2 = PointF()// 贝塞尔曲线结束点

    private val mPath0: Path = Path()
    private val mPath1: Path = Path()
    private val mXORPath: Path = Path()
    private val mPaint: Paint = Paint()
    private var mDegrees = 0f
    private var mMaxLength = 0f
    private var mTouchToCornerDis: Float = 0f

    private var mBackShadowDrawableLR // 有阴影的GradientDrawable
            : GradientDrawable? = null
    private var mBackShadowDrawableRL: GradientDrawable? = null
    private var mFolderShadowDrawableLR: GradientDrawable? = null
    private var mFolderShadowDrawableRL: GradientDrawable? = null

    private var mFrontShadowDrawableHBT: GradientDrawable? = null
    private var mFrontShadowDrawableHTB: GradientDrawable? = null
    private var mFrontShadowDrawableVLR: GradientDrawable? = null
    private var mFrontShadowDrawableVRL: GradientDrawable? = null

    private var mColorMatrixFilter: ColorMatrixColorFilter? = null
    private var mMatrix: Matrix? = null
    private val mMatrixArray = floatArrayOf(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 1.0f)
    private var isPre: Boolean? = null
    override fun isPre(): Boolean {
        return isPre ?: false
    }

    private var isCancel = false
    override fun isCancel(): Boolean {
        return isCancel
    }

    private var mStartX = 0f
    private var mStartY = 0f

    init {
        mPaint.style = Paint.Style.FILL
        val cm = ColorMatrix() //设置颜色数组

        val array = floatArrayOf(
            1f, 0f, 0f, 0f, 0f, 0f,
            1f, 0f, 0f, 0f, 0f, 0f,
            1f, 0f, 0f, 0f, 0f, 0f,
            1f, 0f
        )
        cm.set(array)
        mColorMatrixFilter = ColorMatrixColorFilter(cm)
        mMatrix = Matrix()

        mTouchX = 0.01f // 不让x,y为0,否则在点计算时会有问题

        mTouchY = 0.01f
    }

    override fun initAnim(context: Context, w: Int, h: Int) {
        super.initAnim(context, w, h)
        mMaxLength = Math.hypot(mViewWidth.toDouble(), mViewHeight.toDouble()).toFloat()
        createDrawable()
    }


    /**
     * 创建阴影的GradientDrawable
     */
    private fun createDrawable() {
        val color = intArrayOf(0x333333, -0x4fcccccd)
        mFolderShadowDrawableRL = GradientDrawable(
            GradientDrawable.Orientation.RIGHT_LEFT, color
        )
        mFolderShadowDrawableRL?.setGradientType(GradientDrawable.LINEAR_GRADIENT)
        mFolderShadowDrawableLR = GradientDrawable(
            GradientDrawable.Orientation.LEFT_RIGHT, color
        )
        mFolderShadowDrawableLR?.setGradientType(GradientDrawable.LINEAR_GRADIENT)

        // 背面颜色组
        val mBackShadowColors = intArrayOf(-0xeeeeef, 0x111111)
        mBackShadowDrawableRL = GradientDrawable(
            GradientDrawable.Orientation.RIGHT_LEFT, mBackShadowColors
        )
        mBackShadowDrawableRL?.setGradientType(GradientDrawable.LINEAR_GRADIENT)
        mBackShadowDrawableLR = GradientDrawable(
            GradientDrawable.Orientation.LEFT_RIGHT, mBackShadowColors
        )
        mBackShadowDrawableLR?.setGradientType(GradientDrawable.LINEAR_GRADIENT)

        // 前面颜色组
        val mFrontShadowColors = intArrayOf(-0x7feeeeef, 0x111111)
        mFrontShadowDrawableVLR = GradientDrawable(
            GradientDrawable.Orientation.LEFT_RIGHT, mFrontShadowColors
        )
        mFrontShadowDrawableVLR?.setGradientType(GradientDrawable.LINEAR_GRADIENT)
        mFrontShadowDrawableVRL = GradientDrawable(
            GradientDrawable.Orientation.RIGHT_LEFT, mFrontShadowColors
        )
        mFrontShadowDrawableVRL?.setGradientType(GradientDrawable.LINEAR_GRADIENT)
        mFrontShadowDrawableHTB = GradientDrawable(
            GradientDrawable.Orientation.TOP_BOTTOM, mFrontShadowColors
        )
        mFrontShadowDrawableHTB?.setGradientType(GradientDrawable.LINEAR_GRADIENT)
        mFrontShadowDrawableHBT = GradientDrawable(
            GradientDrawable.Orientation.BOTTOM_TOP, mFrontShadowColors
        )
        mFrontShadowDrawableHBT?.setGradientType(GradientDrawable.LINEAR_GRADIENT)
    }

    override fun touchDown(x: Float, y: Float) {
        mStartX = x
        mStartY = y
        isPre = null
    }

    override fun touchMove() {
        if (isPre == null) {
            isPre = mTouchX > mStartX
            if (isPre == true) { //pre

                if (mStartY > mViewHeight / 3) {
                    calcCornerXY(0f, mViewHeight.toFloat())
                } else {
                    calcCornerXY(0f, 0f)
                }
            } else { // next

                if (mViewHeight / 3 < mStartY) {
                    calcCornerXY(mViewWidth.toFloat(), mViewHeight.toFloat())
                } else {
                    calcCornerXY(mViewWidth.toFloat(), 0f)
                }
            }
        }
        //上一页滑动不出现对角

        Log.i("pageview", "simulatePage touchMove 1 $mTouchY")
        if (mStartY > mViewHeight / 3 && mStartY < mViewHeight * 2 / 3) {
            mTouchY = mViewHeight.toFloat()//这个是为了点击中间区域直接显示全翻篇效果
        }
        Log.i("pageview", "simulatePage touchMove $mTouchY")
    }

    override fun touchUp() {
        if (isPre == true) { //
            isCancel = mTouchX < mStartX
        } else {
            isCancel = mTouchX > mStartX
        }

    }


    override fun draw(canvas: Canvas, curBitmap: Bitmap, nextBitmap: Bitmap) {
        calcPoints()
        drawCurrentPageArea(canvas, curBitmap)
        drawNextPageAreaAndShadow(canvas, nextBitmap)
        drawCurrentPageShadow(canvas)
        drawCurrentBackArea(canvas, curBitmap)
    }

    private fun calcCornerXY(x: Float, y: Float) {
        mCornerX = x.toInt()
        mCornerY = y.toInt()
        mIsRTandLB = (mCornerX == 0 && mCornerY == mViewHeight
                || mCornerX == mViewWidth && mCornerY == 0)
    }


    private fun calcPoints() {
        mMiddleX = (mTouchX + mCornerX) / 2
        mMiddleY = (mTouchY + mCornerY) / 2
        mBezierControl1.x =
            mMiddleX - (mCornerY - mMiddleY) * (mCornerY - mMiddleY) / (mCornerX - mMiddleX)
        mBezierControl1.y = mCornerY.toFloat()
        mBezierControl2.x = mCornerX.toFloat()

        var f4 = mCornerY.toFloat() - mMiddleY
        if (f4 == 0.0f) {
            mBezierControl2.y = mMiddleY - (mCornerX - mMiddleX) * (mCornerX - mMiddleX) / 0.1f
        } else {
            mBezierControl2.y =
                mMiddleY - (mCornerX - mMiddleX) * (mCornerX - mMiddleX) / (mCornerY - mMiddleY)
        }
        mBezierStart1.x = mBezierControl1.x - (mCornerX - mBezierControl1.x) / 2
        mBezierStart1.y = mCornerY.toFloat()

        // 当mBezierStart1.x < 0或者mBezierStart1.x > 480时
        // 如果继续翻页，会出现BUG故在此限制
        if (mTouchX > 0 && mTouchX < mViewWidth) {
            Log.i("PageView", "mBezierStart1 : ${mBezierStart1.x}")
            if (mBezierStart1.x < 0 || mBezierStart1.x > mViewWidth) {
                if (mBezierStart1.x < 0) mBezierStart1.x = mViewWidth - mBezierStart1.x
                val f1 = abs(mCornerX - mTouchX)
                val f2: Float = mViewWidth * f1 / mBezierStart1.x
                mTouchX = abs(mCornerX - f2)
                val f3 = Math.abs(mCornerX - mTouchX) * Math.abs(mCornerY - mTouchY) / f1
                mTouchY = Math.abs(mCornerY - f3)
                mMiddleX = (mTouchX + mCornerX) / 2
                mMiddleY = (mTouchY + mCornerY) / 2
                mBezierControl1.x =
                    mMiddleX - (mCornerY - mMiddleY) * (mCornerY - mMiddleY) / (mCornerX - mMiddleX)
                mBezierControl1.y = mCornerY.toFloat()
                mBezierControl2.x = mCornerX.toFloat()
                val f5 = mCornerY - mMiddleY
                if (f5 == 0f) {
                    mBezierControl2.y =
                        mMiddleY - (mCornerX - mMiddleX) * (mCornerX - mMiddleX) / 0.1f
                } else {
                    mBezierControl2.y =
                        mMiddleY - (mCornerX - mMiddleX) * (mCornerX - mMiddleX) / (mCornerY - mMiddleY)
                }
                mBezierStart1.x = (mBezierControl1.x
                        - (mCornerX - mBezierControl1.x) / 2)
            }
        }
        mBezierStart2.x = mCornerX.toFloat()
        mBezierStart2.y = mBezierControl2.y - (mCornerY - mBezierControl2.y) / 2

        mTouchToCornerDis = hypot(
            (mTouchX - mCornerX).toDouble(),
            (mTouchY - mCornerY).toDouble()
        ).toFloat()

        mBezierEnd1 = getCross(
            PointF(mTouchX, mTouchY), mBezierControl1, mBezierStart1,
            mBezierStart2
        )
        mBezierEnd2 = getCross(
            PointF(mTouchX, mTouchY), mBezierControl2, mBezierStart1,
            mBezierStart2
        )

        mBezierVertex1.x = (mBezierStart1.x + 2 * mBezierControl1.x + mBezierEnd1.x) / 4
        mBezierVertex1.y = (2 * mBezierControl1.y + mBezierStart1.y + mBezierEnd1.y) / 4
        mBezierVertex2.x = (mBezierStart2.x + 2 * mBezierControl2.x + mBezierEnd2.x) / 4
        mBezierVertex2.y = (2 * mBezierControl2.y + mBezierStart2.y + mBezierEnd2.y) / 4
    }

    private fun drawCurrentPageArea(canvas: Canvas, bitmap: Bitmap) {

        mPath0.reset()
        mPath0.moveTo(mBezierStart1.x, mBezierStart1.y)
        mPath0.quadTo(
            mBezierControl1.x, mBezierControl1.y, mBezierEnd1.x,
            mBezierEnd1.y
        )
        Log.i("PageView", "mBezierStart1 : ${mBezierStart1.x} ${mBezierStart1.y}")
        Log.i("PageView", "mBezierControl1 : ${mBezierControl1.x} ${mBezierControl1.y}")
        Log.i("PageView", "mBezierEnd1 : ${mBezierEnd1.x} ${mBezierEnd1.y}")
        Log.i("PageView", "mBezierEnd2 : ${mBezierEnd2.x} ${mBezierEnd2.y}")
        Log.i("PageView", "mTouch : $mTouchX $mTouchY")
        Log.i("PageView", "mBezierControl2 : ${mBezierControl2.x} ${mBezierControl2.y}")
        Log.i("PageView", "mBezierStart2 : ${mBezierStart2.x} ${mBezierStart2.y}")
        Log.i("PageView", "mCorner : $mCornerX $mCornerY")
        mPath0.lineTo(mTouchX, mTouchY)
        mPath0.lineTo(mBezierEnd2.x, mBezierEnd2.y)
        mPath0.quadTo(
            mBezierControl2.x, mBezierControl2.y, mBezierStart2.x,
            mBezierStart2.y
        )
        mPath0.lineTo(mCornerX.toFloat(), mCornerY.toFloat())
        mPath0.close()
        canvas.save()

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
//            mXORPath.reset()
//            mXORPath.moveTo(0f, 0f)
//            mXORPath.lineTo(canvas.width.toFloat(), 0f)
//            mXORPath.lineTo(canvas.width.toFloat(), canvas.height.toFloat())
//            mXORPath.lineTo(0f, canvas.height.toFloat())
//            mXORPath.close()
//
//            // 取 path 的补给，作为 canvas 的交集
//            mXORPath.op(mPath0, Path.Op.XOR)
//            canvas.clipPath(mXORPath)
//        } else {
//            canvas.clipPath(mPath0, Region.Op.XOR)
//        }
        canvas.drawBitmap(bitmap, 0f, 0f, null)

        canvas.restore()

    }

    private fun drawNextPageAreaAndShadow(canvas: Canvas, bitmap: Bitmap) {
        mPath1.reset()
        mPath1.moveTo(mBezierStart1.x, mBezierStart1.y)
        mPath1.lineTo(mBezierVertex1.x, mBezierVertex1.y)
        mPath1.lineTo(mBezierVertex2.x, mBezierVertex2.y)
        mPath1.lineTo(mBezierStart2.x, mBezierStart2.y)
        mPath1.lineTo(mCornerX.toFloat(), mCornerY.toFloat())
        mPath1.close()
        mDegrees = Math.toDegrees(
            Math.atan2(
                (mBezierControl1.x
                        - mCornerX).toDouble(), (mBezierControl2.y - mCornerY).toDouble()
            )
        ).toFloat()
        val leftx: Int
        val rightx: Int
        val mBackShadowDrawable: GradientDrawable
        if (mIsRTandLB) {  //左下及右上
            leftx = mBezierStart1.x.toInt()
            rightx = (mBezierStart1.x + mTouchToCornerDis / 4).toInt()
            mBackShadowDrawable = mBackShadowDrawableLR!!
        } else {
            leftx = (mBezierStart1.x - mTouchToCornerDis / 4).toInt()
            rightx = mBezierStart1.x.toInt()
            mBackShadowDrawable = mBackShadowDrawableRL!!
        }
        canvas.save()

        canvas.clipPath(mPath0)
        canvas.clipPath(mPath1, Region.Op.INTERSECT)

        canvas.drawBitmap(bitmap, 0f, 0f, null)
        canvas.rotate(mDegrees, mBezierStart1.x, mBezierStart1.y)
        mBackShadowDrawable.setBounds(
            leftx, mBezierStart1.y.toInt(), rightx,
            (mMaxLength + mBezierStart1.y).toInt()
        ) //左上及右下角的xy坐标值,构成一个矩形
        mBackShadowDrawable.draw(canvas)
        canvas.restore()
    }

    /**
     * 绘制翻起页的阴影
     *
     * @param canvas
     */
    private fun drawCurrentPageShadow(canvas: Canvas) {
        val degree: Double
        if (mIsRTandLB) {
            degree = (Math.PI
                    / 4
                    - atan2(
                (mBezierControl1.y - mTouchY).toDouble(), (mTouchX
                        - mBezierControl1.x).toDouble()
            ))
        } else {
            degree = (Math.PI
                    / 4
                    - Math.atan2(
                (mTouchY - mBezierControl1.y).toDouble(), (mTouchX
                        - mBezierControl1.x).toDouble()
            ))
        }
        // 翻起页阴影顶点与touch点的距离
        val d1 = 25.toFloat() * 1.414 * Math.cos(degree)
        val d2 = 25.toFloat() * 1.414 * Math.sin(degree)
        val x = (mTouchX + d1).toFloat()
        val y: Float
        if (mIsRTandLB) {
            y = (mTouchY + d2).toFloat()
        } else {
            y = (mTouchY - d2).toFloat()
        }
        mPath1.reset()
        mPath1.moveTo(x, y)
        mPath1.lineTo(mTouchX, mTouchY)
        mPath1.lineTo(mBezierControl1.x, mBezierControl1.y)
        mPath1.lineTo(mBezierStart1.x, mBezierStart1.y)
        mPath1.close()
        canvas.save()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            mXORPath.reset()
            mXORPath.moveTo(0f, 0f)
            mXORPath.lineTo(canvas.width.toFloat(), 0f)
            mXORPath.lineTo(canvas.width.toFloat(), canvas.height.toFloat())
            mXORPath.lineTo(0f, canvas.height.toFloat())
            mXORPath.close()

            // 取 path 的补集，作为 canvas 的交集
            mXORPath.op(mPath0, Path.Op.XOR)
            canvas.clipPath(mXORPath)
        } else {
            canvas.clipPath(mPath0, Region.Op.XOR)
        }
        canvas.clipPath(mPath1, Region.Op.INTERSECT)

        var leftx: Int
        var rightx: Int
        var mCurrentPageShadow: GradientDrawable
        if (mIsRTandLB) {
            leftx = mBezierControl1.x.toInt()
            rightx = mBezierControl1.x.toInt() + 25
            mCurrentPageShadow = mFrontShadowDrawableVLR!!
        } else {
            leftx = (mBezierControl1.x - 25).toInt()
            rightx = mBezierControl1.x.toInt() + 1
            mCurrentPageShadow = mFrontShadowDrawableVRL!!
        }
        var rotateDegrees: Float = Math.toDegrees(
            Math.atan2(
                (mTouchX
                        - mBezierControl1.x).toDouble(), (mBezierControl1.y - mTouchY).toDouble()
            )
        ).toFloat()
        canvas.rotate(rotateDegrees, mBezierControl1.x, mBezierControl1.y)
        mCurrentPageShadow.setBounds(
            leftx,
            (mBezierControl1.y - mMaxLength).toInt(), rightx,
            (mBezierControl1.y).toInt()
        )
        mCurrentPageShadow.draw(canvas)
        canvas.restore()
        mPath1.reset()
        mPath1.moveTo(x, y)
        mPath1.lineTo(mTouchX, mTouchY)
        mPath1.lineTo(mBezierControl2.x, mBezierControl2.y)
        mPath1.lineTo(mBezierStart2.x, mBezierStart2.y)
        mPath1.close()
        canvas.save()
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                mXORPath.reset()
                mXORPath.moveTo(0f, 0f)
                mXORPath.lineTo(canvas.width.toFloat(), 0f)
                mXORPath.lineTo(canvas.width.toFloat(), canvas.height.toFloat())
                mXORPath.lineTo(0f, canvas.height.toFloat())
                mXORPath.close()

                // 取 path 的补给，作为 canvas 的交集
                mXORPath.op(mPath0, Path.Op.XOR)
                canvas.clipPath(mXORPath)
            } else {
                canvas.clipPath(mPath0, Region.Op.XOR)
            }
            canvas.clipPath(mPath1, Region.Op.INTERSECT)
        } catch (e: java.lang.Exception) {
        }
        if (mIsRTandLB) {
            leftx = (mBezierControl2.y).toInt()
            rightx = (mBezierControl2.y + 25).toInt()
            mCurrentPageShadow = (mFrontShadowDrawableHTB)!!
        } else {
            leftx = (mBezierControl2.y - 25).toInt()
            rightx = (mBezierControl2.y + 1).toInt()
            mCurrentPageShadow = (mFrontShadowDrawableHBT)!!
        }
        rotateDegrees = Math.toDegrees(
            Math.atan2(
                ((mBezierControl2.y
                        - mTouchY).toDouble()), (mBezierControl2.x - mTouchX).toDouble()
            )
        ).toFloat()
        canvas.rotate(rotateDegrees, mBezierControl2.x, mBezierControl2.y)
        val temp: Float
        if (mBezierControl2.y < 0) temp = mBezierControl2.y - mViewHeight else temp =
            mBezierControl2.y
        val hmg = Math.hypot(mBezierControl2.x.toDouble(), temp.toDouble()).toInt()
        if (hmg > mMaxLength) mCurrentPageShadow
            .setBounds(
                (mBezierControl2.x - 25).toInt() - hmg, leftx,
                (mBezierControl2.x + mMaxLength).toInt() - hmg,
                rightx
            ) else mCurrentPageShadow.setBounds(
            (mBezierControl2.x - mMaxLength).toInt(), leftx,
            (mBezierControl2.x).toInt(), rightx
        )
        mCurrentPageShadow.draw(canvas)
        canvas.restore()
    }

    /**
     * 绘制翻起页背面
     *
     * @param canvas
     * @param bitmap
     */
    private fun drawCurrentBackArea(canvas: Canvas, bitmap: Bitmap) {
        val i = (mBezierStart1.x + mBezierControl1.x).toInt() / 2
        val f1 = Math.abs(i - mBezierControl1.x)
        val i1 = (mBezierStart2.y + mBezierControl2.y).toInt() / 2
        val f2 = Math.abs(i1 - mBezierControl2.y)
        val f3 = Math.min(f1, f2)
        mPath1.reset()
        mPath1.moveTo(mBezierVertex2.x, mBezierVertex2.y)
        mPath1.lineTo(mBezierVertex1.x, mBezierVertex1.y)
        mPath1.lineTo(mBezierEnd1.x, mBezierEnd1.y)
        mPath1.lineTo(mTouchX, mTouchY)
        mPath1.lineTo(mBezierEnd2.x, mBezierEnd2.y)
        mPath1.close()
        val mFolderShadowDrawable: GradientDrawable
        val left: Int
        val right: Int
        if (mIsRTandLB) {
            left = (mBezierStart1.x - 1).toInt()
            right = (mBezierStart1.x + f3 + 1).toInt()
            mFolderShadowDrawable = mFolderShadowDrawableLR!!
        } else {
            left = (mBezierStart1.x - f3 - 1).toInt()
            right = (mBezierStart1.x + 1).toInt()
            mFolderShadowDrawable = mFolderShadowDrawableRL!!
        }
        canvas.save()

        canvas.clipPath(mPath0)
        canvas.clipPath(mPath1, Region.Op.INTERSECT)

        mPaint.colorFilter = mColorMatrixFilter
        //对Bitmap进行取色
        val color = bitmap.getPixel(1, 1)
        //获取对应的三色
        val red = color and 0xff0000 shr 16
        val green = color and 0x00ff00 shr 8
        val blue = color and 0x0000ff
        //转换成含有透明度的颜色
        val tempColor = Color.argb(200, red, green, blue)
        val dis = hypot(
            (mCornerX - mBezierControl1.x).toDouble(), (
                    mBezierControl2.y - mCornerY).toDouble()
        ).toFloat()
        val f8 = (mCornerX - mBezierControl1.x) / dis
        val f9 = (mBezierControl2.y - mCornerY) / dis
        mMatrixArray[0] = 1 - 2 * f9 * f9
        mMatrixArray[1] = 2 * f8 * f9
        mMatrixArray[3] = mMatrixArray.get(1)
        mMatrixArray[4] = 1 - 2 * f8 * f8
        mMatrix?.apply {
            reset()
            setValues(mMatrixArray)
            preTranslate(-mBezierControl1.x, -mBezierControl1.y)
            postTranslate(mBezierControl1.x, mBezierControl1.y)
            canvas.drawBitmap(bitmap, this, mPaint)
        }
        //背景叠加
        canvas.drawColor(tempColor)
        mPaint.colorFilter = null
        canvas.rotate(mDegrees, mBezierStart1.x, mBezierStart1.y)
        mFolderShadowDrawable.setBounds(
            left, mBezierStart1.y.toInt(), right,
            (mBezierStart1.y + mMaxLength).toInt()
        )
        mFolderShadowDrawable.draw(canvas)
        canvas.restore()
    }

    override fun scrolledPosition(): Point {
        var dx: Int
        val dy: Int
        // dx 水平方向滑动的距离，负值会使滚动向左滚动
        // dy 垂直方向滑动的距离，负值会使滚动向上滚动
        if (isCancel) {
            dx = if (mCornerX > 0 && isPre == false) {
                (mViewWidth - mTouchX.toInt())
            } else {
                (-mTouchX).toInt()
            }
            if (isPre == true) {
                dx = -(mViewWidth + mTouchX.toInt())
            }
            dy = if (mCornerY > 0) {
                (mViewHeight - mTouchY.toInt())
            } else {
                (-mTouchY).toInt() // 防止mTouchY最终变为0
            }
        } else {
            dx = if (mCornerX > 0
                && isPre == false
            ) {
                -(mViewWidth + mTouchX.toInt())
            } else {
                (mViewWidth - mTouchX.toInt() + mViewWidth)
            }
            dy = if (mCornerY > 0) {
                (mViewHeight - mTouchY.toInt())
            } else {
                (1 - mTouchY).toInt() // 防止mTouchY最终变为0
            }
        }
        return Point().apply {
            x = dx
            y = dy
        }

    }


    /**
     * 求解直线P1P2和直线P3P4的交点坐标
     *
     * @param P1
     * @param P2
     * @param P3
     * @param P4
     * @return
     */
    private fun getCross(P1: PointF, P2: PointF, P3: PointF, P4: PointF): PointF {
        val crossP = PointF()
        // 二元函数通式： y=ax+b
        val a1 = (P2.y - P1.y) / (P2.x - P1.x)
        val b1 = (P1.x * P2.y - P2.x * P1.y) / (P1.x - P2.x)
        val a2 = (P4.y - P3.y) / (P4.x - P3.x)
        val b2 = (P3.x * P4.y - P4.x * P3.y) / (P3.x - P4.x)
        crossP.x = (b2 - b1) / (a1 - a2)
        crossP.y = a1 * crossP.x + b1
        return crossP
    }
}