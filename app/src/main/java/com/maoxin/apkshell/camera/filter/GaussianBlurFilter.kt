package com.maoxin.apkshell.camera.filter

import android.content.Context
import android.opengl.GLES30
import com.maoxin.apkshell.camera.GlUtils
import com.maoxin.apkshell.utils.CommonUtils
import kotlin.math.cos

/**
 * 底层提供的均值模糊，来自videotemplatelibs#GaussianBlurFilter
 * @author lmx
 * Created by lmx on 2020/9/13.
 */
class GaussianBlurFilter : BaseFilter() {

    private var vGaussianSigma = 0
    private var vTexture = 0

    private var progress: Float = 0.5f

    override fun getTextureType(): Int {
        return GLES30.GL_TEXTURE_2D
    }

    override fun onCreate(context: Context) {
        program = GlUtils.loadProgram(
                String(CommonUtils.readAssetFile(context, "camera/example1/filter/general_vertex.glsl")!!),
                String(CommonUtils.readAssetFile(context, "camera/example1/filter/fragment_gaussian_blur.glsl")!!)
        )
        GLES30.glUseProgram(program)
        vGaussianSigma = GLES30.glGetUniformLocation(program, "GaussianSigma")
        vTexture = GLES30.glGetUniformLocation(program, "vTexture")
    }

    override fun onDraw(width: Int, height: Int, commonVao: Int, vararg textures: Int): Int {

        bufferMgr?.apply {
            bindNext()
        }

        var textureId = textures[0]
        GLES30.glUseProgram(program)
        GLES30.glActiveTexture(GLES30.GL_TEXTURE0)
        GLES30.glBindTexture(getTextureType(), textureId)
        GLES30.glUniform1i(vTexture, 0)
        GLES30.glUniform1f(vGaussianSigma, getBlur())

        GLES30.glBindVertexArray(commonVao)
        GLES30.glDrawArrays(GLES30.GL_TRIANGLE_STRIP, 0, 4)

        //clear
        GLES30.glBindVertexArray(0)
        GLES30.glBindTexture(getTextureType(), 0)

        bufferMgr?.apply {
            unbind()
            textureId = getCurrentTextureId()
        }

        GLES30.glUseProgram(0)
        return textureId
    }

    private fun getBlur(): Float {
        return (-1f / 2.0f * (cos(Math.PI * progress / 0.5f) - 1.0f) + 0f).toFloat() * 0.45f
    }
}