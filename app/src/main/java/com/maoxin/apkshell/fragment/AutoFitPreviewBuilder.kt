package com.maoxin.apkshell.fragment

import android.view.Display
import android.view.Surface
import android.view.TextureView
import androidx.camera.core.PreviewConfig
import java.lang.ref.WeakReference

/** @author lmx
 * Created by lmx on 2019-06-02.
 */
class AutoFitPreviewBuilder private constructor(config: PreviewConfig, viewRef: WeakReference<TextureView>) {


    companion object {
        fun GetDisplaySurfaceRotation(display: Display) {
            when (display?.rotation) {
                Surface.ROTATION_0 -> 0
                Surface.ROTATION_90 -> 90
                Surface.ROTATION_180 -> 180
                Surface.ROTATION_270 -> 270
                else -> null
            }
        }


        fun Build(config: PreviewConfig, viewRef: WeakReference<TextureView>): AutoFitPreviewBuilder {
            return AutoFitPreviewBuilder(config, viewRef)
        }

        fun Build2(config: PreviewConfig, viewRef: WeakReference<TextureView>) = AutoFitPreviewBuilder(config, viewRef)
    }
}