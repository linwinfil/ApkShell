package com.maoxin.apkshell.camera

import android.graphics.SurfaceTexture
import android.view.Surface

/** @author lmx
 * Created by lmx on 2020/9/1.
 */
class SurfaceObj {

    var surfaceTexture: SurfaceTexture? = null
    var surface: Surface? = null
    var textureId: Int = GlUtils.NO_TEXTURE

    fun genSurface() {
        // 创建一个OES纹理id
        textureId = GlUtils.createTextureOES()
        surfaceTexture = SurfaceTexture(textureId)
        surface = Surface(surfaceTexture)
    }

    fun release() {
        textureId = GlUtils.NO_TEXTURE
        surfaceTexture?.release()
        surfaceTexture = null
        surface?.release()
        surface = null
    }
}
