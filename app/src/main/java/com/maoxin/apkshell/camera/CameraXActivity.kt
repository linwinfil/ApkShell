package com.maoxin.apkshell.camera

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.SurfaceTexture
import android.os.Bundle
import android.os.Environment
import android.os.Looper
import android.util.Size
import android.view.Surface
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.util.Consumer
import com.google.common.util.concurrent.ListenableFuture
import com.maoxin.apkshell.R
import org.jetbrains.anko.toast
import java.io.File

class CameraXActivity : AppCompatActivity(), View.OnClickListener {


    companion object {
        const val PERMISSION_REQUEST_CODE = 0x12

        //1、PreviewView；2、SurfaceView；3、TextureView
        const val MAX_TYPE_VALUE = 2
    }

    var imageCapture: ImageCapture? = null
    var camera: Camera? = null

    var lensFacing: Int = CameraSelector.LENS_FACING_BACK

    lateinit var rootView: ConstraintLayout
    lateinit var btnSwitchLens: Button
    lateinit var btnImageCapture: Button
    lateinit var btnSwitchView: Button

    var viewId: Int = 0

    var glThread: GLThread? = null
    var surfaceTexture: SurfaceTexture? = null
    var surface: Surface? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera_x)
        rootView = findViewById(R.id.camera_x_root)
        btnSwitchLens = findViewById(R.id.btn_switch_lens)
        btnImageCapture = findViewById(R.id.btn_image_capture)
        btnSwitchView = findViewById(R.id.btn_switch_view)
        btnSwitchView.tag = 3
        btnSwitchView.setOnClickListener(this)
        btnSwitchLens.setOnClickListener(this)
        btnImageCapture.setOnClickListener(this)
        viewId = View.generateViewId()

        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.CAMERA), PERMISSION_REQUEST_CODE)
        } else {
            startCamera(btnSwitchView.tag as Int)
        }
    }

    override fun onClick(v: View?) {
        v?.apply {
            when (id) {
                R.id.btn_switch_lens -> {
                    switchLens()
                }
                R.id.btn_image_capture -> {
                    takePhoto()
                }
                R.id.btn_switch_view -> {
                    var newType = (v.tag as Int) + 1
                    if (newType > MAX_TYPE_VALUE) {
                        newType = 1
                    }
                    v.tag = newType
                    startCamera(newType)
                }
            }
        }
    }

    private fun dropGL(tag: Int) {
        when (tag) {
            1 -> {
            }
            2,
            3 -> {
                glThread?.also {
                    it.release()
                }
                glThread = null
                surfaceTexture?.also {
                    it.release()
                }
                surfaceTexture = null
                surface?.also {
                    it.release()
                }
                surface = null
            }
        }
    }

    private fun replaceView(tag: Int): View? {
        val view = rootView.findViewById<View>(viewId)
        rootView.removeView(view)

        var addView: View? = null
        when (tag) {
            1 -> {
                addView = PreviewView(this)
            }
            2 -> {
                addView = AutoFixSurfaceView(this).apply {
                    holder.addCallback(glThread)
                }
            }
            3 -> {
                addView = AutoFixTextureView(this).apply {
                    surfaceTextureListener = glThread
                }
            }
        }
        addView?.also {
            it.id = viewId
            rootView.addView(it)
            if (tag == 1) {
                ConstraintSet().apply {
                    connect(viewId, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 0)
                    connect(viewId, ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.LEFT, 0)
                    connect(viewId, ConstraintSet.RIGHT, ConstraintSet.PARENT_ID, ConstraintSet.RIGHT, 0)
                    connect(viewId, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 0)
                    constrainWidth(viewId, ConstraintSet.MATCH_CONSTRAINT)
                    constrainHeight(viewId, ConstraintSet.MATCH_CONSTRAINT)
                }.applyTo(rootView)
            }
        }
        return addView
    }

    /**
     * @param tag 1:PreviewView
     *            2:SurfaceView
     *            3:TextureView
     */
    @SuppressLint("Recycle")
    private fun startCamera(tag: Int) {
        dropGL(tag)
        when (tag) {
            2, 3 -> {
                glThread = GLThread(this@CameraXActivity).apply {
                    start()
                }
            }
        }
        var previewView = replaceView(tag)
        assert(previewView != null)

        // 摄像头统一管理
        val cameraProviderFuture: ListenableFuture<ProcessCameraProvider> =
                ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener(Runnable {
            val provider: ProcessCameraProvider = cameraProviderFuture.get()
            provider.unbindAll() //调用前先解绑？

            //拍照
            imageCapture = ImageCapture.Builder()
                    .setTargetRotation(windowManager.defaultDisplay.rotation)
                    .setFlashMode(ImageCapture.FLASH_MODE_AUTO)
                    .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                    .build()

            //相机参数设置
            val selector: CameraSelector = CameraSelector.Builder()
                    .requireLensFacing(lensFacing)
                    .build()


            //构建预览
            if (previewView is PreviewView) {
                val preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(previewView.createSurfaceProvider())
                }
                //将生命周期附加到相机实例中
                try {
                    camera = provider.bindToLifecycle(this@CameraXActivity, selector, preview, imageCapture)
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
            } else if (previewView is AutoFixSurfaceView) {
                val preview = Preview.Builder().build()
                try {
                    //将生命周期附加到相机实例中
                    camera = provider.bindToLifecycle(this@CameraXActivity, selector, preview, imageCapture)
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
                preview.setSurfaceProvider(glThread!!, Preview.SurfaceProvider { request ->
                    // 提供渲染surface
                    println("call -> onSurfaceRequested")
                    val size: Size = request.resolution

                    runOnUiThread {
                        previewView.setSize(rootView, size.width, size.height)
                        previewView.requestLayout()
                    }

                    if (surface == null) {
                        glThread?.apply {
                            setCameraSize(size.width, size.height)
                            setCameraRotation(camera!!.cameraInfo.sensorRotationDegrees)
                            surfaceTexture = SurfaceTexture(this.oesTextureId)
                            surfaceTexture!!.setDefaultBufferSize(size.width, size.height)
                            surfaceTexture!!.setOnFrameAvailableListener(this, this.getHandler())
                        }
                        surface = Surface(surfaceTexture)
                    }
                    request.provideSurface(surface!!, glThread!!, Consumer { result: SurfaceRequest.Result? ->
                        println("provideSurface result " + Thread.currentThread().id)
                    })

                })
            } else if (previewView is AutoFixTextureView) {
                val preview = Preview.Builder().build()
                try {
                    //将生命周期附加到相机实例中
                    camera = provider.bindToLifecycle(this@CameraXActivity, selector, preview, imageCapture)
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }

                preview.setSurfaceProvider(glThread!!, Preview.SurfaceProvider { request ->
                    // 提供渲染surface
                    println("call -> onSurfaceRequested")
                    val size: Size = request.resolution
                    val previewSize = PreviewUtils.getPreviewSize(previewView, size)
                    runOnUiThread {
                        previewView.setSize(rootView, size.width, size.height)
                        previewView.requestLayout()
                    }

                    if (surface == null) {
                        glThread?.apply {
                            setCameraSize(previewSize.width, previewSize.height)
                            setCameraRotation(camera!!.cameraInfo.sensorRotationDegrees)
                            surfaceTexture = SurfaceTexture(this.oesTextureId)
                            surfaceTexture!!.setDefaultBufferSize(size.width, size.height)
                            surfaceTexture!!.setOnFrameAvailableListener(this, this.getHandler())
                        }
                        surface = Surface(surfaceTexture)
                    }
                    request.provideSurface(surface!!, glThread!!, Consumer { result: SurfaceRequest.Result? ->
                        println("provideSurface result " + Thread.currentThread().id)
                    })

                })
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun switchLens() {
        lensFacing = if (lensFacing == CameraSelector.LENS_FACING_BACK) {
            CameraSelector.LENS_FACING_FRONT
        } else {
            CameraSelector.LENS_FACING_BACK
        }
        startCamera(btnSwitchView.tag as Int)
    }

    private fun takePhoto() {
        imageCapture?.also {
            val outOption = ImageCapture.OutputFileOptions
                    .Builder(getOutImgCaptureFile())
                    .build()
            it.takePicture(outOption, ContextCompat.getMainExecutor(this),
                    object : ImageCapture.OnImageSavedCallback {
                        override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                            println("main thread:${Thread.currentThread().id == Looper.getMainLooper().thread.id}")
                            toast(outputFileResults.savedUri.toString())
                        }

                        override fun onError(exception: ImageCaptureException) {
                            exception.printStackTrace()
                            toast("error")
                        }
                    })

        }
    }

    private fun getOutImgCaptureFile(): File {
        val absolutePath = this.getExternalFilesDir(Environment.DIRECTORY_DCIM)!!.absolutePath
        val file = File(absolutePath)
        if (!file.exists()) {
            file.mkdir()
        }
        return File(absolutePath,
                System.currentTimeMillis().toString() + ".jpg"
        )
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (allPermissionsGranted()) {
                startCamera(btnSwitchView.tag as Int)
            } else {
                Toast.makeText(this, "相机授权失败", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun allPermissionsGranted(): Boolean {
        return ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
    }


}