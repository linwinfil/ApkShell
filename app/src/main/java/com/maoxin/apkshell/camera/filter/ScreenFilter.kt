package com.maoxin.apkshell.camera.filter

import android.content.Context
import android.opengl.GLES30
import com.maoxin.apkshell.camera.GlUtils
import com.maoxin.apkshell.utils.CommonUtils

/** @author lmx
 * Created by lmx on 2020/9/7.
 */
class ScreenFilter : BaseFilter() {
    override fun onCreate(context: Context) {
        program = GlUtils.loadProgram(
                String(CommonUtils.readAssetFile(context, "camera/example1/screen/screen_vertex.glsl")!!),
                String(CommonUtils.readAssetFile(context, "camera/example1/screen/screen_fragment.glsl")!!)
        )
    }

    override fun onDraw(width: Int, height: Int, commonVao: Int, vararg textures: Int): Int {
        GLES30.glViewport(0, 0, viewWidth, viewHeight)
        //不需要绘制到FBO上
        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, 0)

        GLES30.glClearColor(0.1f, 0.1f, 0.1f, 1.0f)
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT)

        //启动绘制纹理
        GLES30.glUseProgram(program)
        GLES30.glActiveTexture(GLES30.GL_TEXTURE0)
        val textureId = textures[0]
        GLES30.glBindTexture(getTextureType(), textureId)

        //绘制顶点
        GLES30.glBindVertexArray(commonVao)
        GLES30.glDrawArrays(GLES30.GL_TRIANGLE_STRIP, 0, 4)

        //解绑
        GLES30.glBindVertexArray(0)
        GLES30.glBindTexture(getTextureType(), 0)
        GLES30.glUseProgram(0)
        return textureId
    }

    override fun getTextureType(): Int {
        return GLES30.GL_TEXTURE_2D
    }
}