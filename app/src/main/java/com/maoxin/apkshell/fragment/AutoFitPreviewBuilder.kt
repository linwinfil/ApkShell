package com.maoxin.apkshell.fragment

import android.util.Size
import android.view.Display
import android.view.TextureView
import android.view.ViewGroup
import androidx.camera.core.Preview
import androidx.camera.core.PreviewConfig
import java.lang.ref.WeakReference
import java.util.*

/** @author lmx
 * Created by lmx on 2019-06-02.
 */
class AutoFitPreviewBuilder private constructor(config: PreviewConfig, viewRef: WeakReference<TextureView>) {

    private var rotation: Int? = null //可能空
    private var displayId: Int = -1

    var bufferDimens: Size = Size(0, 0)
    var textureViewDimens: Size = Size(0, 0)
    var bufferRotation: Int = 0
    private var userCase: Preview

    init {
        val textureView = viewRef.get() ?: throw IllegalStateException("texture view is null")
        displayId = textureView.display.displayId
        rotation = GetDisplaySurfaceRotation(textureView.display)

        userCase = Preview(config)
        userCase.onPreviewOutputUpdateListener = Preview.OnPreviewOutputUpdateListener {
            var textureView = viewRef.get() ?: return@OnPreviewOutputUpdateListener //返回表达式调用者
            val parent = textureView.parent as ViewGroup
            parent.removeView(textureView)
            parent.addView(textureView, 0)

            textureView.surfaceTexture = it.surfaceTexture
            bufferRotation = it.rotationDegrees

            var rotation = GetDisplaySurfaceRotation(textureView.display)
        }
    }

    private fun updateTransform(textureView: TextureView?, rotation: Int?, newBufferDimens: Size, newTextureViewDimens: Size) {
        var textureView = textureView ?: return

        if (rotation == this.rotation && Objects.equals(newBufferDimens, bufferDimens) && Objects.equals(newTextureViewDimens, textureViewDimens)) {
            return
        }
        if (rotation == null) return
        else this.rotation = rotation

        if (newBufferDimens.width == 0 || newBufferDimens.height == 0) return
        else textureViewDimens = newBufferDimens

    }

    companion object {
        //获取屏幕旋转角度
        fun GetDisplaySurfaceRotation(display: Display?):Int {
            //when (display?.rotation) {
            //    Surface.ROTATION_0 -> 0
            //    Surface.ROTATION_90 -> 90
            //    Surface.ROTATION_180 -> 180
            //    Surface.ROTATION_270 -> 270
            //    else -> null
            //}
            return 0
        }


        fun Build(config: PreviewConfig, viewRef: WeakReference<TextureView>): AutoFitPreviewBuilder {
            return AutoFitPreviewBuilder(config, viewRef)
        }

        fun Build2(config: PreviewConfig, viewRef: WeakReference<TextureView>) = AutoFitPreviewBuilder(config, viewRef)
    }
}