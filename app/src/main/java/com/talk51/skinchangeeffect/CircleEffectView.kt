package com.talk51.skinchangeeffect

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.app.Activity
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewGroup

/**
 * Created by wanghao on 2018/4/26.
 * 圆形扩散效果
 */
class CircleEffectView : View {

    val TAG = "CircleEffectView"

    private lateinit var mPaint: Paint
    private var mBackground: Bitmap? = null

    private var mCircleLayer: Bitmap? = null
    private var mCanvas: Canvas? = null


    private var mRootView: ViewGroup? = null
    /**
     * 标识动画是否已经开始
     */
    private var isStarted: Boolean = false

    /**
     * 起始半径
     */
    private var mMaxRadius: Float = 0f
    /**
     * 起始半径
     */
    private var mStartRadius: Float = 0f
    /**
     * 当前半径
     */
    private var mCurRadius: Float = 0f


    /**
     * 点击坐标
     */
    private var mStartX: Float = 0f
    private var mStartY: Float = 0f

    private val mMode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)

    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initPaint()
    }

    private fun initPaint() {
        mPaint = Paint(android.graphics.Paint.ANTI_ALIAS_FLAG)
    }

    private fun initBuffer(src: Bitmap) {
        if (mCanvas == null) {
            mCircleLayer = Bitmap.createBitmap(src.width, src.height, Bitmap.Config.ARGB_8888)
            mCircleLayer?.eraseColor(Color.TRANSPARENT)
            mCanvas = Canvas(mCircleLayer)
        }
    }

    override fun onDraw(canvas: Canvas?) {
        canvas?.drawBitmap(mCircleLayer, 0f, 0f, null)
    }

    /**
     * 启动动画
     */
    fun start(clickView: View?) {
        if (isStarted || clickView == null) {
            return
        }
        updateBackground()
        //计算圆形半径和坐标
        updateMaxRadius(clickView)
        attachViewToRoot()
        startAnimator()
    }

    private fun updateBackground() {
        val act = context
        if (act is Activity) {
            mRootView = act.window.decorView as ViewGroup
        }

        mRootView?.let {
            it.isDrawingCacheEnabled = true
            it.buildDrawingCache()
            val cache = it.drawingCache
            mBackground = Bitmap.createBitmap(cache)
            initBuffer(cache)
            it.destroyDrawingCache()
        }

    }

    /**
     * 计算圆最大半径
     */
    private fun updateMaxRadius(clickView: View) {
        //计算最小半径
        val W = clickView.width / 2
        val H = clickView.height / 2

        mStartRadius = Math.max(W, H).toFloat()

        //计算最大圆的半径
        val location = IntArray(2)
        clickView.getLocationOnScreen(location)
        val x = location[0]
        val y = location[1]

        mStartX = (x + W).toFloat()
        mStartY = (y + H).toFloat()

        //左上rect
        val ltRect = RectF(0f, 0f, x + mStartRadius, y + mStartRadius)
        //右上rect
        val rtRect = RectF(ltRect.right, 0f, mRootView!!.right.toFloat(), ltRect.bottom)
        //坐下rect
        val lbRect = RectF(0f, ltRect.bottom, ltRect.right, mRootView!!.bottom.toFloat())
        //右下rect
        val rbRect = RectF(ltRect.right, ltRect.bottom, rtRect.right, lbRect.bottom)

        val lt = Math.sqrt(Math.pow(ltRect.width().toDouble(), 2.0) + Math.pow(ltRect.height().toDouble(), 2.0))
        val rt = Math.sqrt(Math.pow(rtRect.width().toDouble(), 2.0) + Math.pow(rtRect.height().toDouble(), 2.0))

        val lb = Math.sqrt(Math.pow(lbRect.width().toDouble(), 2.0) + Math.pow(lbRect.height().toDouble(), 2.0))
        val rb = Math.sqrt(Math.pow(rbRect.width().toDouble(), 2.0) + Math.pow(rbRect.height().toDouble(), 2.0))

        mMaxRadius = Math.max(Math.max(lt, rt), Math.max(lb, rb)).toFloat()

    }

    /**
     * 添加当前view到window
     */
    private fun attachViewToRoot() {
        mRootView?.let {
            val params = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT)
            it.addView(this, params)
        }
    }

    /**
     * 开启属性动画
     */
    private fun startAnimator() {
        val animator = ValueAnimator.ofFloat(mStartRadius, mMaxRadius)
        animator.duration = 500
        animator.addUpdateListener {
            mCurRadius = animator.animatedValue as Float
            Log.d(TAG, "mCurRadius = $mCurRadius")
            drawCircle2Buffer()
            postInvalidate()
        }

        animator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                isStarted = false
                mRootView?.removeView(this@CircleEffectView)
                Log.d(TAG, "onAnimationEnd = $mCurRadius")
            }

            override fun onAnimationStart(animation: Animator?) {
                isStarted = true
                Log.d(TAG, "onAnimationEnd = $mCurRadius")
            }
        })
        animator.start()
    }

    private fun drawCircle2Buffer() {
        mCanvas?.drawBitmap(mBackground, 0f, 0f, null)
        mPaint.xfermode = mMode
        mCanvas?.drawCircle(mStartX, mStartY, mCurRadius, mPaint)
        mPaint.xfermode = null
    }
}