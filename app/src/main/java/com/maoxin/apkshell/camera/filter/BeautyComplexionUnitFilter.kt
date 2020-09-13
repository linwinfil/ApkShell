package com.maoxin.apkshell.camera.filter

import android.content.Context
import android.opengl.GLES30
import com.maoxin.apkshell.camera.GlUtils
import com.maoxin.apkshell.utils.CommonUtils
import kotlin.math.max
import kotlin.math.min

/**
 * 美肤滤镜
 * @author lmx
 * Created by lmx on 2020/9/9.
 * @see <a href="https://github.com/CainKernel/CainCamera">算法来自CainCamera</a>
 */
class BeautyComplexionUnitFilter : BaseFilter() {

    private var vInputTexture = -1
    private var vGrayTexture = -1
    private var vLookupTexture = -1
    private var vLevelRangeInv = -1
    private var vLevelBlack = -1
    private var vAlpha = -1

    private var skinGray = -1
    private var skinLut = -1

    private var levelRangeInv = 1.040816f
    private var levelBlack = 0.01960784f
    var alpha = 0.6f
        set(value) {
            field = min(1f, max(0f, value))
        }

    override fun getTextureType(): Int {
        return GLES30.GL_TEXTURE_2D
    }

    override fun onCreate(context: Context) {
        program = GlUtils.loadProgram(
                String(CommonUtils.readAssetFile(context, "camera/example1/filter/general_vertex.glsl")!!),
                String(CommonUtils.readAssetFile(context, "camera/example1/filter/fragment_beauty_complexion.glsl")!!)
        )

        skinGray = GlUtils.createTextureFromAssets(context, "camera/example1/filter/skin_gray.png")
        skinLut = GlUtils.createTextureFromAssets(context, "camera/example1/filter/skin_lookup.png")

        GLES30.glUseProgram(program)
        vInputTexture = GLES30.glGetUniformLocation(program, "inputTexture")
        vGrayTexture = GLES30.glGetUniformLocation(program, "grayTexture")
        vLookupTexture = GLES30.glGetUniformLocation(program, "lookupTexture")
        vLevelRangeInv = GLES30.glGetUniformLocation(program, "levelRangeInv")
        vLevelBlack = GLES30.glGetUniformLocation(program, "levelBlack")
        vAlpha = GLES30.glGetUniformLocation(program, "alpha")
        GLES30.glUseProgram(0)
    }

    override fun onDraw(width: Int, height: Int, commonVao: Int, vararg textures: Int): Int {
        GLES30.glViewport(0, 0, viewWidth, viewHeight)

        bufferMgr?.apply {
            bindNext()
        }

        var textureId = textures[0]
        GLES30.glClearColor(0.5f, 0.5f, 0.5f, 1f)
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT)

        GLES30.glUseProgram(program)
        GLES30.glActiveTexture(GLES30.GL_TEXTURE0)
        GLES30.glBindTexture(getTextureType(), textureId)
        GLES30.glUniform1i(vInputTexture, 0)

        GLES30.glActiveTexture(GLES30.GL_TEXTURE1)
        GLES30.glBindTexture(getTextureType(), skinGray)
        GLES30.glUniform1i(vGrayTexture, 1)

        GLES30.glActiveTexture(GLES30.GL_TEXTURE2)
        GLES30.glBindTexture(getTextureType(), skinLut)
        GLES30.glUniform1i(vLookupTexture, 2)

        GLES30.glUniform1f(vLevelRangeInv, levelRangeInv)
        GLES30.glUniform1f(vLevelBlack, levelBlack)
        GLES30.glUniform1f(vAlpha, alpha)

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

    override fun onDestroy(context: Context) {
        if (skinGray > -1) {
            GLES30.glDeleteTextures(1, IntArray(skinGray), 0)
            skinGray = -1
        }
        if (skinLut > -1) {
            GLES30.glDeleteTextures(1, IntArray(skinLut), 0)
            skinLut = -1
        }
        super.onDestroy(context)
    }
}