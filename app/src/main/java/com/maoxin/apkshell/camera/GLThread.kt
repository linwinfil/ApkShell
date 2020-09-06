package com.maoxin.apkshell.camera

import android.content.Context
import android.graphics.SurfaceTexture
import android.opengl.EGL14
import android.opengl.GLES11Ext
import android.opengl.GLES30
import android.opengl.Matrix
import android.os.Handler
import android.os.HandlerThread
import android.view.SurfaceHolder
import android.view.TextureView
import com.android.grafika.gles.EglCore
import com.android.grafika.gles.WindowSurface
import com.maoxin.apkshell.R
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.util.concurrent.Executor

/** @author lmx
 * Created by lmx on 2020/8/30.
 */
class GLThread(context: Context) : HandlerThread("-glThread-"),
        Executor, SurfaceHolder.Callback, SurfaceTexture.OnFrameAvailableListener, TextureView.SurfaceTextureListener {

    var oesTextureId = GlUtils.NO_TEXTURE

    private var context: Context = context.applicationContext
    private var handler: Handler? = null
    private var eglCore: EglCore? = null
    private var windowSurface: WindowSurface? = null
    private var glMatrixT = GlMatrixTools()
    private var viewW: Int = 0
    private var viewH: Int = 0
    private var cameraW: Int = 0
    private var cameraH: Int = 0
    private var cameraR: Int = 0
    private val stMatrix = FloatArray(16)


    @Volatile
    private var isDestroy: Boolean = false

    //顶点坐标
    val positionPoint = floatArrayOf(
            -1.0f, -1.0f,   // 0 左下角
            1.0f, -1.0f,   // 1 右下角
            -1.0f, 1.0f,   // 2 左上角
            1.0f, 1.0f   // 3 右上角
    )

    //纹理坐标（对应顶点坐标）
    val coordinatePoint = floatArrayOf(
            0f, 0f,
            1.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 1.0f)

    var postionBuf: FloatBuffer? = null
    var coordinateBuf: FloatBuffer? = null

    var programHandle: Int = 0
    var vertexShaderHandle: Int = 0
    var fragmentShaderHandle: Int = 0

    //顶点坐标
    var positionHandle: Int = 0

    //纹理坐标矩
    var coordinateHandle: Int = 0

    //变换矩阵
    var matrixHandle: Int = 0

    //当前纹理局柄
    var textureHandle: Int = 0

    //oes纹理矩阵
    var oesTexHandle: Int = 0

    fun getHandler(): Handler {
        if (this.handler == null) {
            handler = Handler(looper)
        }
        return handler!!
    }

    override fun start() {
        super.start()
        getHandler().post {
            //创建egl环境
            eglCore = EglCore(EGL14.EGL_NO_CONTEXT, EglCore.FLAG_TRY_GLES3)
            oesTextureId = GlUtils.createTextureOES()
            Matrix.setIdentityM(stMatrix, 0)
        }
    }

    fun setCameraSize(cameraW: Int, cameraH: Int) {
        this.cameraW = cameraW
        this.cameraH = cameraH
    }

    fun setCameraRotation(cameraR: Int) {
        this.cameraR = cameraR
    }

    fun swapBuffers() {
        if (!isDestroy) {
            windowSurface?.swapBuffers()
        }
    }

    fun releaseRaw() {
        windowSurface?.also {
            it.release()
        }
        windowSurface = null
        if (programHandle != 0) {
            GLES30.glDeleteProgram(programHandle)
            programHandle = 0
        }
    }

    fun release() {
        handler?.post(this::releaseRaw)
        isDestroy = true
        quitSafely()
    }

    override fun execute(command: Runnable?) {
        if (!isDestroy) {
            command?.apply {
                getHandler().post(this)
            }
        }
    }

    override fun onFrameAvailable(surfaceTexture: SurfaceTexture?) {
        println("call -> onFrameAvailable")
        //更新缓冲区
        synchronized(this) {
            surfaceTexture!!.updateTexImage()
            surfaceTexture.getTransformMatrix(stMatrix)
            //gl绘制
            drawFrame()
            //交换显存（将surface显存和显示器的显存交换）
            swapBuffers()
        }
    }

    override fun surfaceCreated(holder: SurfaceHolder?) {
        println("call -> surfaceCreated")
        isDestroy = false
        handler?.apply {
            post {
                releaseRaw()
                windowSurface = WindowSurface(eglCore, holder!!.surface, false).apply {
                    makeCurrent()
                }
                glCreated()
                onSizeChanged()
            }
        }
    }

    override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
        println("call -> surfaceChanged")
        viewW = width
        viewH = height
        if (!isDestroy) {
            handler?.post { glChanged(width, height) }
        }
    }

    override fun surfaceDestroyed(holder: SurfaceHolder?) {
        println("call -> surfaceDestroyed")
        isDestroy = true
    }

    // === start  TextureView.SurfaceTextureListener ===
    override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture?, width: Int, height: Int) {
        println("call -> onSurfaceTextureSizeChanged")
        if (!isDestroy) {
            handler?.post {
                viewW = width
                viewH = height
                onSizeChanged()
            }
        }
    }

    override fun onSurfaceTextureUpdated(surface: SurfaceTexture?) {
    }

    override fun onSurfaceTextureDestroyed(surface: SurfaceTexture?): Boolean {
        isDestroy = true
        return true
    }

    override fun onSurfaceTextureAvailable(surface: SurfaceTexture?, width: Int, height: Int) {
        println("call -> onSurfaceTextureAvailable")
        handler?.post {
            viewW = width
            viewH = height
            onSizeChanged()

            releaseRaw()
            windowSurface = WindowSurface(eglCore, surface).apply {
                makeCurrent()
            }
            glCreated()
        }
    }
    // === end  TextureView.SurfaceTextureListener ===

    private fun glCreated() {

        //加载着色器
        val vertexShader = GlUtils.loadShaderRawResource(context, R.raw.oes_vertex_shader)
        val fragShader = GlUtils.loadShaderRawResource(context, R.raw.oes_fragment_shader)

        postionBuf = ByteBuffer.allocateDirect(positionPoint.size * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer().put(positionPoint)
        postionBuf!!.position(0)

        coordinateBuf = ByteBuffer.allocateDirect(coordinatePoint.size * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer().put(coordinatePoint)
        coordinateBuf!!.position(0)

        //加载program
        vertexShaderHandle = GlUtils.loadShader(vertexShader, GLES30.GL_VERTEX_SHADER)
        fragmentShaderHandle = GlUtils.loadShader(fragShader, GLES30.GL_FRAGMENT_SHADER)
        programHandle = GlUtils.loadProgram(vertexShaderHandle, fragmentShaderHandle)

        positionHandle = GLES30.glGetAttribLocation(programHandle, "vPosition")
        coordinateHandle = GLES30.glGetAttribLocation(programHandle, "vCoordinate")
        matrixHandle = GLES30.glGetUniformLocation(programHandle, "vMatrix")
        textureHandle = GLES30.glGetUniformLocation(programHandle, "vTexture")
        oesTexHandle = GLES30.glGetUniformLocation(programHandle, "vTexMatrix")
    }

    private fun glChanged(width: Int, height: Int) {
        onSizeChanged()
    }

    private fun onSizeChanged() {
        if (cameraW > 0 && cameraH > 0 && viewW > 0 && viewH > 0) {

            //eyez <= near < far
            val near = 3.0f
            val far = 9.0f
            val eyez = near

            //设置相机位置
            glMatrixT.setCamera(
                    0f, 0f, eyez,  //相机位置（eyez <= far）
                    0f, 0f, 0f,  //观察点
                    0f, 1f, 0f //辅助向上量
            )

            //如果宽度大于高度，如果以宽度以-1到1为基准，则高度为-1 * height/width 到 1 * height/width
            //反之亦然
            val cameraWH: Float = cameraW * 1f / cameraH
            val viewWH = viewW * 1f / viewH
            if (cameraWH > viewWH) {
                glMatrixT.frustum(-1f, 1f, -cameraWH / viewWH, cameraWH / viewWH, near, far)
            } else {
                glMatrixT.frustum(-viewWH / cameraWH, viewWH / cameraWH, -1f, 1f, near, far)
            }
        }
    }

    fun drawFrame() {
        GLES30.glViewport(0, 0, viewW, viewH)

        GLES30.glClearColor(1f, 1f, 1f, 1f)
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT or GLES30.GL_DEPTH_BUFFER_BIT)

        //加载程序
        GLES30.glUseProgram(programHandle)

        //绑定纹理
        GLES30.glActiveTexture(GLES30.GL_TEXTURE0)
        GLES30.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, oesTextureId)
        //给纹理单元分配一个默认值
        GLES30.glUniform1i(textureHandle, 0)

        //给视图矩阵赋值
        GLES30.glUniformMatrix4fv(matrixHandle, 1, false, glMatrixT.finalMatrix, 0)
        //oes纹理绑定
        GLES30.glUniformMatrix4fv(oesTexHandle, 1, false, stMatrix, 0)


        //启用顶点坐标句柄
        GLES30.glEnableVertexAttribArray(positionHandle)
        //传入顶点坐标数据
        GLES30.glVertexAttribPointer(positionHandle, 2, GLES30.GL_FLOAT, false, 2 * 4, postionBuf)
        //启用纹理坐标句柄
        GLES30.glEnableVertexAttribArray(coordinateHandle)
        //传如纹理坐标数据
        GLES30.glVertexAttribPointer(coordinateHandle, 2, GLES30.GL_FLOAT, false, 2 * 4, coordinateBuf)

        //绘制模式
        GLES30.glDrawArrays(GLES30.GL_TRIANGLE_STRIP, 0, 4)

        //解绑坐标，解绑 纹理
        GLES30.glDisableVertexAttribArray(positionHandle)
        GLES30.glDisableVertexAttribArray(coordinateHandle)
        GLES30.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, 0)
    }

}