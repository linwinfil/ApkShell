package com.maoxin.apkshell.camera

import android.content.Context
import android.renderscript.ScriptIntrinsicConvolve3x3
import android.view.SurfaceView
import android.view.TextureView
import android.view.View
import kotlin.math.min

/** @author lmx
 * Created by lmx on 2020/8/30.
 */
class AutoFixTextureView(context: Context?) : TextureView(context) {
    var mParent: View? = null
    var mWidth: Int = 0
    var mHeight: Int = 0

    public fun setSize(parent: View, width: Int, height: Int) {
        mParent = parent
        mWidth = width
        mHeight = height
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if (mParent != null) {
            var w: Int
            var h: Int
            if (PreviewUtils.isNaturalPortrait(this)) {
                w = mHeight
                h = mWidth
            } else {
                w = mWidth
                h = mHeight
            }
            val scale = min(mParent!!.width.toFloat() / w, mParent!!.height.toFloat() / h)
            w = (w * scale + 0.5f).toInt()
            h = (h * scale + 0.5f).toInt()
            this.setMeasuredDimension(w, h)
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        }

    }
}