package com.maoxin.apkshell.camera.filter

import android.opengl.GLES20
import android.opengl.GLES30
import kotlin.math.max

/** @author lmx
 * Created by lmx on 2020/9/7.
 */
open class GLFrameBufferMgr {

    @Volatile
    private var bufferArr: IntArray? = null

    @Volatile
    private var colorTextureArr: IntArray? = null

    @Volatile
    private var currentIndex: Int = -1

    @Volatile
    private var bufferSize: Int = 1

    @Volatile
    private var bufferWidth: Int = 100

    @Volatile
    private var bufferHeight: Int = 100

    constructor(width: Int, height: Int, size: Int = 1) {
        bufferWidth = width
        bufferHeight = height
        bufferSize = size
        bufferArr = IntArray(size)
        colorTextureArr = IntArray(size)

        //创建frame buffer
        GLES30.glGenFramebuffers(size, bufferArr, 0)

        //创建挂载颜色的纹理
        GLES30.glGenTextures(size, colorTextureArr, 0)

        for (i in 0 until size) {

            //绑定fbo
            val bufferId = bufferArr!![i]
            GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, bufferId)

            // 挂载颜色缓冲纹理
            val textureId = colorTextureArr!![i]
            GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureId)
            GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_LINEAR.toFloat())
            GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR.toFloat())
            GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_S, GLES30.GL_CLAMP_TO_EDGE.toFloat())
            GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_T, GLES30.GL_CLAMP_TO_EDGE.toFloat())
            GLES30.glTexImage2D(GLES30.GL_TEXTURE_2D, 0, GLES30.GL_RGBA, width, height, 0, GLES30.GL_RGBA, GLES30.GL_UNSIGNED_BYTE, null)
            GLES30.glFramebufferTexture2D(GLES30.GL_FRAMEBUFFER, GLES30.GL_COLOR_ATTACHMENT0, GLES30.GL_TEXTURE_2D, textureId, 0)
        }

        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, 0)
        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, 0)
        GLES30.glBindRenderbuffer(GLES30.GL_RENDERBUFFER, 0)
    }


    open fun bindNext() {
        bindNext(GLES30.GL_NONE)
    }

    open fun bindNext(textureId: Int) {
        if (checkAvailable()) {
            bindNext(currentIndex, textureId)
        }
    }

    private fun bindNext(index: Int, textureId: Int) {
        val newIndex = checkNextIndex(index)
        //绑定frame buffer
        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, bufferArr!![newIndex])
        if (textureId != GLES30.GL_NONE) {
            GLES30.glFramebufferTexture2D(GLES30.GL_FRAMEBUFFER, GLES30.GL_COLOR_ATTACHMENT0, GLES30.GL_TEXTURE_2D, textureId, 0)
            GLES30.glColorMask(true, true, true, true)
        } else {
            //挂载到一个纹理上
            GLES30.glFramebufferTexture2D(GLES30.GL_FRAMEBUFFER, GLES30.GL_COLOR_ATTACHMENT0, GLES30.GL_TEXTURE_2D, colorTextureArr!![newIndex], 0)
            //清空之前的颜色纹理
            GLES30.glColorMask(true, true, true, true)
            GLES30.glClearColor(0f, 0f, 0f, 0f)
            GLES30.glClear(GLES20.GL_COLOR_BUFFER_BIT)
            if (GLES30.glCheckFramebufferStatus(GLES30.GL_FRAMEBUFFER) != GLES30.GL_FRAMEBUFFER_COMPLETE) {
                println("ERROR::FRAMEBUFFER:: Framebuffer is not complete!")
            }
        }
        currentIndex = newIndex
    }

    open fun unbind() {
        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, 0)
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, 0)
        GLES30.glBindRenderbuffer(GLES30.GL_RENDERBUFFER, 0)
    }

    public fun getBufferWidth(): Int {
        return bufferWidth
    }

    public fun getBufferHeight(): Int {
        return bufferHeight
    }

    public fun getCurrentTextureId() = if (checkAvailable()) colorTextureArr!![currentIndex] else 0

    public fun getPreviousTextureId() = if (checkAvailable()) colorTextureArr!![checkPreviousIndex(currentIndex)] else 0

    protected open fun checkNextIndex(index: Int): Int {
        return max(index + 1, 0) % bufferSize
    }

    protected open fun checkPreviousIndex(index: Int): Int {
        return max(index - 1, bufferSize - 1) % bufferSize
    }

    protected open fun checkAvailable(): Boolean {
        return bufferArr != null && bufferArr!!.isNotEmpty()
    }

    open fun onDestroy() {
        val size = bufferSize
        bufferArr?.apply {
            GLES30.glDeleteFramebuffers(size, this, 0)
        }
        colorTextureArr?.apply {
            GLES30.glDeleteTextures(size, this, 0)
        }
    }
}