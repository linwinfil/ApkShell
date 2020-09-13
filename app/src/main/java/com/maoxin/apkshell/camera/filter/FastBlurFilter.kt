package com.maoxin.apkshell.camera.filter

import android.content.Context
import android.opengl.GLES30
import androidx.annotation.IntRange
import com.maoxin.apkshell.camera.GlUtils
import com.maoxin.apkshell.utils.CommonUtils
import kotlin.math.max
import kotlin.math.min

/**
 * 模糊度比高斯模糊要差，运算要比高斯模糊少，来自videotemplatelibs#FastBlurFilter
 * @author lmx
 * Created by lmx on 2020/9/11.
 */
class FastBlurFilter : BaseFilter() {
    private var vInputTexture = 0
    private var vRadius = 0
    private var vWidthOffset = 0
    private var vHeightOffset = 0

    private var radius: Int = 25

    override fun getTextureType(): Int {
        return GLES30.GL_TEXTURE_2D
    }

    override fun onCreate(context: Context) {
        program = GlUtils.loadProgram(
                String(CommonUtils.readAssetFile(context, "camera/example1/filter/general_vertex.glsl")!!),
                String(CommonUtils.readAssetFile(context, "camera/example1/filter/fragment_fast_blur.glsl")!!)
        )
        GLES30.glUseProgram(program)
        vInputTexture = GLES30.glGetUniformLocation(program, "inputTexture")
        vRadius = GLES30.glGetUniformLocation(program, "uRadius")
        vWidthOffset = GLES30.glGetUniformLocation(program, "uWidthOffset")
        vHeightOffset = GLES30.glGetUniformLocation(program, "uHeightOffset")
    }

    override fun onDraw(width: Int, height: Int, commonVao: Int, vararg textures: Int): Int {
        GLES30.glViewport(0, 0, width, height)

        bufferMgr?.apply {
            bindNext()
        }

        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT)
        GLES30.glClearColor(0.5f, 0.5f, 0.5f, 1f)
        var textureId = textures[0]

        GLES30.glUseProgram(program)
        GLES30.glActiveTexture(GLES30.GL_TEXTURE0)
        GLES30.glBindTexture(getTextureType(), textureId)
        GLES30.glUniform1i(vInputTexture, 0)

        GLES30.glUniform1i(vRadius, radius)
        GLES30.glUniform1f(vWidthOffset, 1f / width)
        GLES30.glUniform1f(vHeightOffset, 1f / height)


        GLES30.glBindVertexArray(commonVao)
        GLES30.glDrawArrays(GLES30.GL_TRIANGLE_STRIP, 0, 4)


        //clear
        GLES30.glBindVertexArray(0)
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, 0)

        bufferMgr?.apply {
            unbind()
            textureId = getCurrentTextureId()
        }
        GLES30.glUseProgram(0)

        return textureId
    }

    fun setBlurRadius(@IntRange(from = 0, to = 25) radius: Int) {
        this.radius = min(25, max(0, radius))
    }
}