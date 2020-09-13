package com.maoxin.apkshell.camera.example1

import android.content.Context
import android.graphics.SurfaceTexture
import android.opengl.EGL14
import android.opengl.GLES30
import android.os.Handler
import android.os.HandlerThread
import android.view.SurfaceHolder
import com.android.grafika.gles.EglCore
import com.android.grafika.gles.WindowSurface
import com.maoxin.apkshell.camera.GlUtils
import com.maoxin.apkshell.camera.filter.*
import java.util.concurrent.Executor

/** @author lmx
 * Created by lmx on 2020/9/7.
 */
class GLThread(context: Context) : HandlerThread("gl_thread"),
        Executor, SurfaceTexture.OnFrameAvailableListener, SurfaceHolder.Callback {

    @Volatile
    private var isCompare: Boolean = false

    @Volatile
    private var isDestroy: Boolean = false

    private var handler: Handler? = null
    private var eglCore: EglCore? = null
    private var windowSurface: WindowSurface? = null
    private val appContext = context.applicationContext
    private var viewWidth: Int = 0
    private var viewHeight: Int = 0
    private var cameraWidth: Int = 0
    private var cameraHeight: Int = 0
    private var cameraDegree: Int = 0
    private var oesTextureId: Int = GlUtils.NO_TEXTURE
    private var cameraFilter: CameraFilter = CameraFilter()
    private var screenFilter: ScreenFilter = ScreenFilter()
    private var filters: MutableList<BaseFilter> = mutableListOf()
    private var bufferMgr: GLFrameBufferMgr? = null

    private var vao: Int = -1
    private var vbo: Int = -1

    init {
        // filters.add(LutColorFilter())
        // filters.add(BeautyComplexionUnitFilter())
        // filters.add(FastBlurFilter())
        filters.add(GaussianBlurFilter())
    }


    fun setCameraSize(width: Int, height: Int) {
        cameraWidth = width
        cameraHeight = height
        getHandler().post {
            onSizeChanged()
        }
    }

    fun setCameraDegree(degree: Int) {
        cameraDegree = degree
        getHandler().post {
            onSizeChanged()
        }
    }


    public fun getOesTextureId(): Int = oesTextureId

    fun getHandler(): Handler {
        if (this.handler == null) {
            handler = Handler(looper)
        }
        return handler!!
    }

    fun setCompare(compare: Boolean) {
        getHandler().post {
            this.isCompare = compare
        }
    }

    override fun start() {
        super.start()
        getHandler().post {
            //创建gl环境
            eglCore = EglCore(EGL14.EGL_NO_CONTEXT, EglCore.FLAG_TRY_GLES3)
            oesTextureId = GlUtils.createTextureOES()
        }
    }

    override fun execute(command: Runnable?) {
        if (!isDestroy) {
            command?.also {
                getHandler().post(it)
            }
        }
    }

    private fun onSizeChanged() {
        if (viewWidth > 0 && viewHeight > 0 && cameraWidth > 0 && cameraHeight > 0) {
            if (bufferMgr == null || bufferMgr!!.getBufferWidth() != viewWidth || bufferMgr!!.getBufferHeight() != viewHeight) {
                bufferMgr?.apply {
                    onDestroy()
                }
                bufferMgr = GLFrameBufferMgr(viewWidth, viewHeight, 3).also {
                    cameraFilter.setGLFrameBufferMgr(it)
                    screenFilter.setGLFrameBufferMgr(it)
                    for (filter in filters) {
                        filter.setGLFrameBufferMgr(it)
                    }
                }
            }

            cameraFilter.onSizeChange(appContext, cameraWidth, cameraHeight, viewWidth, viewHeight, cameraDegree, 0)
            screenFilter.onSizeChange(appContext, cameraWidth, cameraHeight, viewWidth, viewHeight, cameraDegree, 0)
            for (filter in filters) {
                filter.onSizeChange(appContext, cameraWidth, cameraHeight, viewWidth, viewHeight, cameraDegree, 0)
            }
        }
    }

    override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
        viewWidth = width
        viewHeight = height
        getHandler().post {
            onSizeChanged()
        }
    }

    override fun surfaceDestroyed(holder: SurfaceHolder?) {
        isDestroy = true
        getHandler().post {
            for (filter in filters) {
                filter.onDestroy(appContext)
            }
            cameraFilter.onDestroy(appContext)
            screenFilter.onDestroy(appContext)
            if (vao > -1) {
                GLES30.glDeleteVertexArrays(1, intArrayOf(vao), 0)
                vao = -1
            }
            if (vbo > -1) {
                GLES30.glDeleteBuffers(1, intArrayOf(vbo), 0)
                vbo = -1
            }
            bufferMgr?.apply {
                onDestroy()
            }
            bufferMgr = null
        }
    }

    override fun surfaceCreated(holder: SurfaceHolder?) {
        holder?.apply {
            getHandler().post {

                windowSurface = WindowSurface(eglCore, this.surface, false).apply {
                    makeCurrent()
                }

                // 创建VAO和VBO
                val params: IntArray = GlUtils.createQuadVertexArrays(0, 1)
                vao = params[0]
                vbo = params[1]

                cameraFilter.onCreate(appContext)
                screenFilter.onCreate(appContext)
                for (filter in filters) {
                    filter.onCreate(appContext)
                }
            }
        }
    }

    override fun onFrameAvailable(surfaceTexture: SurfaceTexture?) {
        //更新缓冲区
        synchronized(this) {
            surfaceTexture!!.updateTexImage()

            //gl绘制

            //镜头
            var textureId = oesTextureId
            textureId = cameraFilter.onDraw(viewWidth, viewHeight, vao, textureId)

            //滤镜
            if (isCompare.not()) {
                for (filter in filters) {
                    textureId = filter.onDraw(viewWidth, viewHeight, vao, textureId)
                }
            }

            //屏幕
            screenFilter.onDraw(viewWidth, viewHeight, vao, textureId)

            //交换显存（将surface显存和显示器的显存交换）
            swapBuffers()
        }
    }

    private fun swapBuffers() {
        if (isDestroy) {
            return
        }
        windowSurface?.apply {
            swapBuffers()
        }
    }
}