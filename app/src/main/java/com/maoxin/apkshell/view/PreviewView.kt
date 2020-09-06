package com.maoxin.apkshell.view

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.animation.Interpolator
import androidx.annotation.NonNull
import cn.poco.utils.ImageUtils

import kotlin.math.min
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sin

/** @author lmx
 * Created by lmx on 2020/8/26.
 */
class PreviewView : View {

    //弹性插值器
    class SpringInterpolator( //动画系数，系数越小，弹性次数越多
            var factor: Float = 0.4f) : Interpolator {

        override fun getInterpolation(input: Float): Float {
            return (2.0.pow(-10 * input.toDouble()) * sin((input - factor / 4) * (2 * Math.PI) / factor) + 1f).toFloat()
        }
    }

    class Shape {
        var ownMatrix: Matrix = Matrix() //图片相对于view的矩阵
        var curMatrix: Matrix = Matrix()
        var imgBmp: Bitmap? = null

        fun isBitmapValid(): Boolean {
            imgBmp ?: return false
            return !imgBmp!!.isRecycled
        }
    }

    lateinit var imgShape: Shape
    lateinit var paint: Paint
    lateinit var imgCenter: Point


    //整体的外部矩阵
    lateinit var outsideMatrix: Matrix
    lateinit var tempMatrix: Matrix
    lateinit var stateRecordMatrix: Matrix
    val maxScale: Float = 3f
    val minSacle: Float = 0.3f
    val defScale: Float = 1f

    var down: PointF = PointF()

    //双指操作
    var downPointer1 = PointF()
    var downPointer2 = PointF()

    var isMoveEvent: Boolean = false
    var isDoAnimation: Boolean = false
    var isTouchImgArea: Boolean = false
    var isEventLock: Boolean = false
    var isInitImg: Boolean = true

    var doubleClickTime: Long = 0

    lateinit var doubleAnimation: ValueAnimator
    lateinit var reboundAnimation: ValueAnimator


    companion object {
        @JvmStatic val TAG = "PreviewView"
    }


    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initData(context)
    }

    private fun initData(context: Context?) {
        imgShape = Shape()
        paint = Paint()
        imgCenter = Point()
        outsideMatrix = Matrix()
        tempMatrix = Matrix()
        stateRecordMatrix = Matrix()
        doubleAnimation = ValueAnimator()
        reboundAnimation = ValueAnimator()
    }

    public fun setImgCenterPoint(@NonNull point: Point) {
        this.imgCenter.set(point.x, point.y)
    }

    public fun setImage(@NonNull bitmap: Bitmap) {
        imgShape.imgBmp = bitmap
        if (isInitImg) {
            initImgMatrix(measuredWidth, measuredHeight)
            update()
        }
    }


    private inline fun resetPaint() {
        paint.apply {
            reset()
            flags = (Paint.ANTI_ALIAS_FLAG.or(Paint.FILTER_BITMAP_FLAG))
            isFilterBitmap = true
            isAntiAlias = true
        }
    }

    private fun mixMatrix(dst: Matrix?, vararg mix: Matrix?) {
        dst ?: return
        dst.apply {
            reset()
            mix.forEach {
                if (it != null) {
                    this.postConcat(it)
                }
            }
        }
    }

    private fun isTouchImgArea(x: Float, y: Float): Boolean {
        if (imgShape.isBitmapValid()) {
            val imgBmp = imgShape.imgBmp!!
            val imgRectF = RectF(0f, 0f, imgBmp.width.toFloat(), imgBmp.height.toFloat())
            val outRectF = RectF()
            imgShape.curMatrix.mapRect(outRectF, imgRectF)
            return outRectF.contains(x, y)
        }
        return false
    }

    private fun getInitImgRect(): RectF? {
        if (imgShape.isBitmapValid()) {
            val imgBmp = imgShape.imgBmp!!
            val out = RectF()
            val src = RectF(0f, 0f, imgBmp.width.toFloat(), imgBmp.height.toFloat())
            mixMatrix(tempMatrix, imgShape.ownMatrix)
            tempMatrix.mapRect(out, src)
            return out
        }
        return null
    }

    //计算能放大或缩小的倍率，然后作用到矩阵上
    private fun compareImageScale(x: Float, y: Float, srcMatrix: Matrix) {
        if (imgShape.isBitmapValid()) {
            val imgBmp = imgShape.imgBmp!!
            mixMatrix(tempMatrix, imgShape.ownMatrix, srcMatrix)

            val imgRectF = RectF(0f, 0f, imgBmp.width.toFloat(), imgBmp.height.toFloat())
            val curRectF = RectF()
            tempMatrix.mapRect(curRectF, imgRectF)

            val initImgRect = getInitImgRect()
            initImgRect?.also {
                var dstScale = 1f
                val scale = curRectF.width() / it.width()
                if (minSacle > scale) {
                    dstScale = minSacle / scale
                } else if (scale > maxScale) {
                    dstScale = maxScale / scale
                }
                if (dstScale != 1f) {
                    srcMatrix.postScale(dstScale, dstScale, x, y)
                }
            }
        }
    }

    private fun setStatusRecord(matrix: Matrix?) {
        matrix?.also {
            stateRecordMatrix.reset()
            stateRecordMatrix.set(it)
        }
    }

    private fun syncRecordStatus(dst: Matrix?) {
        dst?.apply {
            reset()
            set(stateRecordMatrix)
        }
    }

    private fun cancelImgAnimation() {
        if (isDoAnimation) {
            if (doubleAnimation.isStarted || doubleAnimation.isRunning) {
                doubleAnimation.cancel()
            }
            if (reboundAnimation.isStarted || reboundAnimation.isRunning) {
                reboundAnimation.cancel()
            }
        }
    }

    private fun doDoubleClickAnimation(x: Float, y: Float) {
        if (imgShape.isBitmapValid()) {
            val imgBmp = imgShape.imgBmp!!
            val viewRectf = RectF(0f, 0f, measuredWidth.toFloat(), measuredHeight.toFloat())
            val orgRectf = RectF(0f, 0f, imgBmp.width.toFloat(), imgBmp.height.toFloat())
            val curRectf = RectF()
            mixMatrix(tempMatrix, imgShape.ownMatrix, outsideMatrix)
            tempMatrix.mapRect(curRectf, orgRectf)
            val initImgRect = getInitImgRect()
            initImgRect ?: return
            var dstScale = 1f
            var dx = 0f
            var dy = 0f
            val scale = String.format("%.2f", (curRectf.width() / initImgRect.width())).toFloat()
            if (scale == defScale) { //双击放大
                //当前还没到最大倍数，则放大到最大倍
                dstScale = maxScale
                if (dstScale.times(scale) >= maxScale) {
                    dstScale = maxScale.div(scale)
                }
                val tempM = Matrix()
                tempM.set(outsideMatrix)
                tempM.postScale(dstScale, dstScale, x, y)

                mixMatrix(tempMatrix, imgShape.ownMatrix, tempM)
                tempMatrix.mapRect(curRectf, orgRectf)

                val imageCenter = PointF(curRectf.left + curRectf.width() / 2f, curRectf.top + curRectf.height() / 2f)
                //判断当前图片缩放后，是否超过view的宽高
                if (curRectf.width() >= viewRectf.width()) {
                    dx = imgCenter.x - x
                    if (curRectf.left + dx >= viewRectf.left) {
                        dx = viewRectf.left - curRectf.left
                    } else if (curRectf.right + dx <= viewRectf.right) {
                        dx = viewRectf.right - curRectf.right
                    }
                } else {
                    dx = imgCenter.x - imageCenter.x
                    if (curRectf.left + dx < viewRectf.left) {
                        dx = dx + viewRectf.left - (curRectf.left + dx)
                    } else if (curRectf.right + dx > viewRectf.right) {
                        dx = dx + viewRectf.right - (curRectf.right + dx)
                    }
                }

                if (curRectf.height() >= viewRectf.height()) {
                    dy = imgCenter.y - y
                    if (curRectf.top + dy >= viewRectf.top) {
                        dy = viewRectf.top - curRectf.top
                    } else if (curRectf.bottom + dy <= viewRectf.bottom) {
                        dy = viewRectf.bottom - curRectf.bottom
                    }
                } else {
                    dy = imgCenter.y - imageCenter.y
                    if (curRectf.top + dy < viewRectf.top) {
                        dy = dy + viewRectf.top - (curRectf.top + dy)
                    } else if (curRectf.bottom + dy > viewRectf.bottom) {
                        dy = dy + viewRectf.bottom - (curRectf.bottom + dy)
                    }
                }
            } else { //双击还原
                dstScale = defScale / scale
                val tempM = Matrix(outsideMatrix)
                tempM.postScale(dstScale, dstScale, x, y)
                mixMatrix(tempMatrix, imgShape.ownMatrix, tempM)
                tempMatrix.mapRect(curRectf, orgRectf)
                dx = imgCenter.x - (curRectf.left + curRectf.width() / 2f)
                dy = imgCenter.y - (curRectf.top + curRectf.height() / 2f)
            }

            val animationMatrix = Matrix(outsideMatrix)
            val finalDstScale = dstScale - 1f
            val finalX = dx
            val finalY = dy
            doubleAnimation.apply {
                addUpdateListener {
                    if (isDoAnimation) {
                        val animatedValue = it.animatedValue as Float
                        outsideMatrix.apply {
                            reset()
                            set(animationMatrix)
                            postScale(1f + finalDstScale * animatedValue, 1f + finalDstScale * animatedValue, x, y)
                            postTranslate(finalX.times(animatedValue), finalY.times(animatedValue))
                        }
                        update()
                    }
                }
                addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationStart(animation: Animator?) {
                        isDoAnimation = true
                        isEventLock = true
                        Log.i(TAG, "onAnimationStart: double animation start")
                    }

                    override fun onAnimationEnd(animation: Animator?) {
                        isDoAnimation = false
                        isEventLock = false
                        doubleAnimation.removeAllListeners()
                        doubleAnimation.removeAllUpdateListeners()
                        Log.i(TAG, "onAnimationStart: double animation end")
                    }

                    override fun onAnimationCancel(animation: Animator?) {
                        isDoAnimation = false
                        isEventLock = false
                        doubleAnimation.removeAllListeners()
                        doubleAnimation.removeAllUpdateListeners()
                    }
                })
                duration = 300
                setFloatValues(0f, 1f)
                start()
            }
        }
    }

    //回弹效果
    private fun doReboundAnimation() {
        if (!isDoAnimation && imgShape.isBitmapValid()) {
            val imgBmp = imgShape.imgBmp!!
            mixMatrix(tempMatrix, imgShape.ownMatrix, outsideMatrix)
            val viewRectf = RectF(0f, 0f, measuredWidth.toFloat(), measuredHeight.toFloat())
            val orgRectf = RectF(0f, 0f, imgBmp.width.toFloat(), imgBmp.height.toFloat())
            val curRectf = RectF()
            tempMatrix.mapRect(curRectf, orgRectf)

            val curImgCenter = PointF(curRectf.left + curRectf.width() / 2f, curRectf.top + curRectf.height() / 2f)
            val scale = curRectf.width() / viewRectf.width()
            var dstScale = -1f
            if (scale < defScale) {
                //还原成def尺寸
                dstScale = defScale / scale
            }

            var dx = 0f
            var dy = 0f

            val imgRectW: Int = curRectf.width().roundToInt()
            val imgRectH: Int = curRectf.height().roundToInt()
            //图片缩放后，是否超过view的宽高
            if (imgRectW >= viewRectf.width()) { //放大状态下
                if (curRectf.left >= viewRectf.left) {
                    dx = viewRectf.left - curRectf.left
                } else if (curRectf.right <= viewRectf.right) {
                    dx = viewRectf.right - curRectf.right
                }
            } else { //缩小状态下
                dx = imgCenter.x - curImgCenter.x
                if (curRectf.left + dx < viewRectf.left) {
                    dx = viewRectf.left - curRectf.left
                } else if (curRectf.right + dx > viewRectf.right) {
                    dx = viewRectf.right - curRectf.right
                }
            }

            if (imgRectH >= viewRectf.height()) {
                if (curRectf.top >= viewRectf.top) {
                    dy = viewRectf.top - curRectf.top
                } else if (curRectf.bottom <= viewRectf.bottom) {
                    dy = viewRectf.bottom - curRectf.bottom
                }
            } else { //缩小的状态
                dy = imgCenter.y - curImgCenter.y
                if (curRectf.top + dy < viewRectf.top) {
                    dy = viewRectf.top - curRectf.top
                } else if (curRectf.bottom + dy > viewRectf.bottom) {
                    dy = viewRectf.bottom - curRectf.bottom
                }
            }
            val x = dx
            val y = dy
            val ds = dstScale
            val animationMatrix = Matrix(outsideMatrix) //需要还原的外部矩阵
            reboundAnimation.apply {
                addUpdateListener {
                    if (isDoAnimation) {
                        val animatedValue = it.animatedValue as Float
                        outsideMatrix.apply {
                            reset()
                            set(animationMatrix)
                            if (ds != -1f) {
                                val ps = dstScale.times(animatedValue)
                                outsideMatrix.postScale(ps, ps, curImgCenter.x, curImgCenter.y)
                            }
                            val px = x.times(animatedValue)
                            val py = y.times(animatedValue)
                            outsideMatrix.postTranslate(px, py)
                            update()
                        }
                    }
                }
                addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationStart(animation: Animator?) {
                        isDoAnimation = true
                    }

                    override fun onAnimationEnd(animation: Animator?) {
                        isDoAnimation = false
                        reboundAnimation.removeAllListeners()
                        reboundAnimation.removeAllUpdateListeners()
                    }

                    override fun onAnimationCancel(animation: Animator?) {
                        isDoAnimation = false
                        reboundAnimation.removeAllListeners()
                        reboundAnimation.removeAllUpdateListeners()
                    }
                })
                duration = 300
                interpolator = SpringInterpolator(0.8f)
                setFloatValues(0f, 1f)
                start()
            }
        }
    }


    override fun onDraw(canvas: Canvas?) {
        canvas?.apply {
            val saveLayer = saveLayer(null, null)
            drawColor(Color.WHITE) //白色背景
            if (imgShape.isBitmapValid()) {
                resetPaint()
                // 当前图片矩阵和外部矩阵融合
                mixMatrix(imgShape.curMatrix, imgShape.ownMatrix, outsideMatrix)
                this.drawBitmap(imgShape.imgBmp!!, imgShape.curMatrix, paint)
            }
            this.restoreToCount(saveLayer)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(ev: MotionEvent?): Boolean {
        val event:MotionEvent = ev!!
        if (!isEventLock && imgShape.isBitmapValid()) {
            when (event.action.and(MotionEvent.ACTION_MASK)) {
                MotionEvent.ACTION_DOWN -> {
                    //禁止父View拦截我的事件
                    parent?.requestDisallowInterceptTouchEvent(true)
                    down.x = event.getX(0)
                    down.y = event.getY(0)
                    isTouchImgArea = isTouchImgArea(down.x, down.y)
                    isMoveEvent = false

                    //取消回弹或双击
                    cancelImgAnimation()
                    //保存当前的矩阵 用于移动
                    setStatusRecord(outsideMatrix)
                    update()
                }
                MotionEvent.ACTION_UP -> {
                    if (isTouchImgArea) {
                        if (!isMoveEvent) {
                            //如果没有移动
                            val times = System.currentTimeMillis() - doubleClickTime
                            if (times <= 1000) {
                                //双击放大或缩小
                                Log.i(TAG, "onTouchEvent: double click times")
                                doubleClickTime = 0
                                doDoubleClickAnimation(event.x, event.y)
                            } else {
                                doReboundAnimation()
                                doubleClickTime = System.currentTimeMillis()
                                Log.i(TAG, "onTouchEvent: init click times")
                            }
                        } else {
                            doubleClickTime = 0
                            //移动了，做回弹操作
                            doReboundAnimation()
                        }
                    }
                }
                MotionEvent.ACTION_MOVE -> {
                    val dx = event.getX(0) - down.x
                    val dy = event.getY(0) - down.y
                    val moveSpace = ImageUtils.Spacing(dx, dy)
                    isMoveEvent = moveSpace > 60 //移动判断
                     if (event.pointerCount >= 2) { //双指操作
                        if (isTouchImgArea) {
                            val downX = (downPointer1.x + downPointer2.x) / 2f
                            val downY = (downPointer1.y + downPointer2.y) / 2f
                            val moveX = (event.getX(0) + event.getX(1)) / 2f
                            val moveY = (event.getY(0) + event.getY(1)) / 2f
                            val downSpace = ImageUtils.Spacing(downPointer1.x - downPointer2.x,
                                    downPointer1.y - downPointer2.y)

                            val moveSpace = ImageUtils.Spacing(event.getX(0) - event.getX(1),
                                    event.getY(0) - event.getY(1))

                            val dx = moveX - downX
                            val dy = moveY - downY
                            val scale = moveSpace / downSpace //两指down距离和移动距离的比

                            syncRecordStatus(outsideMatrix)//还原down时的矩阵
                            outsideMatrix.postScale(scale, scale, downX, downY)
                            compareImageScale(downX, downY, outsideMatrix)
                            outsideMatrix.postTranslate(dx, dy)
                            update()
                        }

                    } else { //单指操作
                        if (isTouchImgArea) {
                            // if (!isDetectInterceptEvent) {
                            //     parent?.also {
                            //         it.requestDisallowInterceptTouchEvent(true)
                            //         isDetectInterceptEvent = true
                            //     }
                            // }

                            syncRecordStatus(outsideMatrix)
                            outsideMatrix.postTranslate(dx, dy) //整体矩阵的移动
                            update()
                        }
                    }
                }
                MotionEvent.ACTION_POINTER_DOWN -> {
                    //多指down
                    if (isTouchImgArea) {
                        parent?.also {
                            it.requestDisallowInterceptTouchEvent(true)
                        }

                        if (event.pointerCount >= 2) {
                            setStatusRecord(outsideMatrix)
                            event.apply {
                                downPointer1.x = getX(0)
                                downPointer1.y = getY(0)
                                downPointer2.x = getX(1)
                                downPointer2.y = getY(1)
                            }
                        }
                    }
                    doubleClickTime = 0
                    isMoveEvent = true
                }
                MotionEvent.ACTION_POINTER_UP -> {
                    //多指抬起时，event.getPointerCount() 依然是多指的数量，并没有减去 1
                    if (event.pointerCount - 1 >= 2) {
                        var pointerDownCount = 0
                        val actionIndex = event.actionIndex // 获取是那个 手指index 抬起
                        for (i in 0 until event.pointerCount) {
                            if (i == actionIndex) continue
                            //为两个手指设置点
                            if (pointerDownCount <= 0) {
                                downPointer1.set(event.getX(i), event.getY(i))
                            } else {
                                downPointer2.set(event.getX(i), event.getY(i))
                            }
                            pointerDownCount += 1
                            if (pointerDownCount >= 2) break
                        }

                        if (isTouchImgArea) {
                            setStatusRecord(outsideMatrix)
                        }
                    } else { //两个手指
                        var pointerDownCount = 0
                        val actionIndex = event.actionIndex
                        for (i in 0 until event.pointerCount) {
                            if (i == actionIndex) continue //如果当前是起来的pointer，则跳过
                            if (pointerDownCount == 0) { //获取当前按住的point作为down
                                down.set(event.getX(i), event.getY(i))
                            }
                            pointerDownCount += 1
                            if (pointerDownCount >= 1) break
                        }

                        if (isTouchImgArea) {
                            setStatusRecord(outsideMatrix)
                        }
                    }
                    update()
                }
                MotionEvent.ACTION_OUTSIDE,
                MotionEvent.ACTION_CANCEL -> {
                    if (isTouchImgArea) {
                        doubleClickTime = 0
                        cancelImgAnimation()
                        doReboundAnimation()
                    }
                    isTouchImgArea = false
                }
            }
        }
        return !isEventLock
    }

    fun update() {
        this.invalidate()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        imgCenter.x = measuredWidth / 2
        imgCenter.y = measuredHeight / 2
        initImgMatrix(w, h)
    }

    private fun initImgMatrix(w: Int, h: Int) {
        if (isInitImg && imgShape.isBitmapValid()) {
            isInitImg = false
            val mImgBmp = imgShape.imgBmp
            mImgBmp ?: return
            val scale = min(w * 1f / mImgBmp.width, h * 1f / mImgBmp.height)
            val x = imgCenter.x - mImgBmp.width * scale / 2f
            val y = imgCenter.y - mImgBmp.height * scale / 2f
            imgShape.ownMatrix.apply {
                reset()
                postScale(scale, scale)
                postTranslate(x, y)
            }
        }
    }

}