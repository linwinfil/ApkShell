package com.maoxin.apkshell.camera.filter

import android.content.Context
import android.opengl.GLES30
import androidx.annotation.FloatRange
import com.maoxin.apkshell.camera.GlUtils
import com.maoxin.apkshell.utils.CommonUtils
import kotlin.math.max
import kotlin.math.min

/** LUT颜色查表
 * @author lmx
 * Created by lmx on 2020/9/9.
 */
class LutColorFilter : BaseFilter() {

    private var curveTexture = -1
    private var alpha: Float = 1f

    private var vInputImageTexture = -1
    private var vCurveTexture = -1
    private var vIntensity = -1

    override fun onCreate(context: Context) {
        program = GlUtils.loadProgram(
                String(CommonUtils.readAssetFile(context, "camera/example1/filter/general_vertex.glsl")!!),
                String(CommonUtils.readAssetFile(context, "camera/example1/filter/lut64_fragment.glsl")!!)
        )
        curveTexture = GlUtils.createTextureFromAssets(context, "camera/example1/filter/foodie_fr4.png")

        GLES30.glUseProgram(program)
        vInputImageTexture = GLES30.glGetUniformLocation(program, "inputImageTexture")
        vCurveTexture = GLES30.glGetUniformLocation(program, "curveTexture")
        vIntensity = GLES30.glGetUniformLocation(program, "intensity")
    }

    override fun onDraw(width: Int, height: Int, commonVao: Int, vararg textures: Int): Int {
        GLES30.glViewport(0, 0, viewWidth, viewHeight)

        bufferMgr?.apply {
            bindNext()
        }

        GLES30.glClearColor(0.1f, 1.0f, 0.1f, 1.0f)
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT)

        var textureId = textures[0]
        GLES30.glUseProgram(program)

        GLES30.glActiveTexture(GLES30.GL_TEXTURE0)
        GLES30.glBindTexture(getTextureType(), textureId)
        GLES30.glUniform1i(vInputImageTexture, 0)

        GLES30.glActiveTexture(GLES30.GL_TEXTURE2)
        GLES30.glBindTexture(getTextureType(), curveTexture)
        GLES30.glUniform1i(vCurveTexture, 2)

        GLES30.glUniform1f(vIntensity, alpha)

        GLES30.glBindVertexArray(commonVao)
        GLES30.glDrawArrays(GLES30.GL_TRIANGLE_STRIP, 0, 4)

        //解绑
        GLES30.glBindVertexArray(0)
        GLES30.glBindTexture(getTextureType(), 0)

        bufferMgr?.apply {
            unbind()
            textureId = getCurrentTextureId()
        }

        GLES30.glUseProgram(0)

        return textureId
    }

    override fun getTextureType(): Int {
        return GLES30.GL_TEXTURE_2D
    }

    override fun onDestroy(context: Context) {
        if (curveTexture > -1) {
            GLES30.glDeleteTextures(1, intArrayOf(curveTexture), 0)
            curveTexture = -1
        }
        super.onDestroy(context)
    }

    open fun setAlpha(@FloatRange(from = 0.0, to = 1.0) alpha: Float) {
        this.alpha = min(max(0f, alpha), 1f)
        GLES30.glUniform1f(5, this.alpha)
    }
}