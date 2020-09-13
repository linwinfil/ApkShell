package com.maoxin.apkshell.camera.filter

import android.content.Context
import android.opengl.GLES30
import com.maoxin.apkshell.camera.GlMatrixTools

/** @author lmx
 * Created by lmx on 2020/9/7.
 */
abstract class BaseFilter : IFilter, IBuffer {

    protected var program: Int = -1
    protected var matrix: FloatArray = FloatArray(16)
    protected var glMatrix: GlMatrixTools = GlMatrixTools()

    protected var viewWidth: Int = 0
    protected var viewHeight: Int = 0

    protected var bufferMgr: GLFrameBufferMgr? = null

    abstract fun getTextureType(): Int

    override fun onDestroy(context: Context) {
        if (program > -1) {
            GLES30.glDeleteProgram(program)
            program = -1
        }
        bufferMgr = null
        glMatrix.clearStack()
    }

    protected fun onUseProgram() {
        GLES30.glUseProgram(program)
    }

    protected fun onUnUseProgram() {
        GLES30.glUseProgram(0)
    }

    override fun onSizeChange(context: Context, cameraWidth: Int, cameraHeight: Int, viewWidth: Int, viewHeight: Int, degree: Int, flip: Int) {
        this.viewWidth = viewWidth
        this.viewHeight = viewHeight
    }

    override fun setGLFrameBufferMgr(bufferMgr: GLFrameBufferMgr) {
        this.bufferMgr = bufferMgr
    }

    protected fun onClear() {
        GLES30.glClearColor(0.1f, 0.1f, 0.1f, 1.0f)
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT)
    }
}