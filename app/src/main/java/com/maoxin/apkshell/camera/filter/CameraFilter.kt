package com.maoxin.apkshell.camera.filter

import android.content.Context
import android.opengl.GLES11Ext
import android.opengl.GLES30
import android.util.Size
import com.maoxin.apkshell.camera.GlUtils
import com.maoxin.apkshell.utils.CommonUtils

/** @author lmx
 * Created by lmx on 2020/9/7.
 */
class CameraFilter : BaseFilter() {

    override fun onCreate(context: Context) {
        program = GlUtils.loadProgram(
                String(CommonUtils.readAssetFile(context, "camera/example1/camera/camera_vertex.glsl")!!),
                String(CommonUtils.readAssetFile(context, "camera/example1/camera/camera_fragment.glsl")!!)
        )
    }

    override fun onSizeChange(context: Context, cameraWidth: Int, cameraHeight: Int, viewWidth: Int, viewHeight: Int, degree: Int, flip: Int) {
        super.onSizeChange(context, cameraWidth, cameraHeight, viewWidth, viewHeight, degree, flip)
        if (cameraWidth > 0 && cameraHeight > 0 && viewWidth > 0 && viewHeight > 0) {
            glMatrix.reset()
            val cameraWH: Float = cameraWidth * 1f / cameraHeight
            val viewWH = viewWidth * 1f / viewHeight
            if (cameraWH > viewWH) {
                glMatrix.scale(cameraWH / viewWH, 1f, 1f)
            } else {
                glMatrix.scale(1f, viewWH / cameraWH, 1f )
            }
            glMatrix.rotate(degree.toFloat(), 0f, 0f, 1f)
        }
    }

    override fun getTextureType(): Int {
        return GLES11Ext.GL_TEXTURE_EXTERNAL_OES
    }

    override fun onDraw(width: Int, height: Int, commonVao: Int, vararg textures: Int): Int {
        GLES30.glViewport(0, 0, width, height)

        var textureId = textures[0]

        //绑定framebuffer，绘制到FBO上
        bufferMgr?.apply {
            bindNext()
        }

        GLES30.glClearColor(0.1f, 0.1f, 0.1f, 1.0f)
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT)

        //启用oes单元，绘制
        GLES30.glUseProgram(program)
        GLES30.glActiveTexture(GLES30.GL_TEXTURE0)
        GLES30.glBindTexture(getTextureType(), textureId)

        //绘制矩阵
        GLES30.glUniformMatrix4fv(2, 1, false, glMatrix.finalMatrix, 0)

        //绘制顶点数组缓冲对象
        GLES30.glBindVertexArray(commonVao)
        GLES30.glDrawArrays(GLES30.GL_TRIANGLE_STRIP, 0, 4)

        //解绑
        GLES30.glBindVertexArray(0)
        GLES30.glBindTexture(getTextureType(), 0)

        bufferMgr?.apply {
            //解绑FBO，取出挂载颜色的纹理
            unbind()
            textureId = getCurrentTextureId()
        }

        GLES30.glUseProgram(0)
        return textureId
    }
}