package com.maoxin.apkshell.camera.example1

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.SurfaceTexture
import android.os.Bundle
import android.util.Size
import android.view.MotionEvent
import android.view.Surface
import android.view.SurfaceView
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.extensions.HdrImageCaptureExtender
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.util.Consumer
import com.maoxin.apkshell.R
import com.maoxin.apkshell.camera.CameraXActivity
import com.maoxin.apkshell.camera.PreviewUtils
import com.maoxin.apkshell.camera.example1.DemoActivity.Ratio.*
import org.jetbrains.anko.doAsync
import kotlin.math.roundToInt

/**
 *
 * @author lmx on 2020/9/7
 * CameraX+SurfaceView+美颜效果
 */
class DemoActivity : AppCompatActivity() {
    companion object {
        const val PERMISSION_REQUEST_CODE = 0x12
    }

    val surfaceView: SurfaceView by lazy {
        findViewById<SurfaceView>(R.id.surface_view)
    }
    val btnRatio: Button by lazy {
        findViewById<Button>(R.id.btn_ratio)
    }
    val btnCompare: Button by lazy {
        findViewById<Button>(R.id.btn_compare)
    }
    val btnCapture: Button by lazy {
        findViewById<Button>(R.id.btn_image_capture)
    }

    var lensFacing: Int = CameraSelector.LENS_FACING_FRONT
    var glThread: GLThread? = null

    private var surface: Surface? = null
    private var surfaceTexture: SurfaceTexture? = null
    private lateinit var imageCapture: ImageCapture

    private enum class Ratio {
        full, _1_1, _3_4, _9_16
    }

    private val ratioArr: Array<Ratio> = values()
    private var ratioIndex = 0

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_demo)

        glThread = GLThread(this).apply {
            start()
        }

        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.CAMERA), PERMISSION_REQUEST_CODE)
        } else {
            startCamera()
        }

        surfaceView.holder.addCallback(glThread)
        btnCompare.setOnTouchListener { view: View?, event: MotionEvent ->
            when (event.action and MotionEvent.ACTION_MASK) {
                MotionEvent.ACTION_DOWN -> glThread?.setCompare(true)
                MotionEvent.ACTION_OUTSIDE,
                MotionEvent.ACTION_UP,
                MotionEvent.ACTION_CANCEL -> glThread?.setCompare(false)
            }
            true
        }

        btnRatio.text = full.toString()
        btnRatio.setOnClickListener {
            val ratio = ratioArr[++ratioIndex % ratioArr.size]
            btnRatio.text = ratio.toString().replaceFirst("_", "").replace("_", " : ")
            val parent: ConstraintLayout = surfaceView.parent as ConstraintLayout
            val viewId = surfaceView.id
            var w = ConstraintSet.MATCH_CONSTRAINT
            var h = ConstraintSet.MATCH_CONSTRAINT
            when (ratio) {
                full -> {
                    w = ConstraintSet.MATCH_CONSTRAINT
                    h = ConstraintSet.MATCH_CONSTRAINT
                }
                _1_1 -> {
                    w = (parent.width shr 1) shl 1
                    h = w
                }
                _3_4 -> {
                    w = (parent.width shr 1) shl 1
                    h = ((w / 3.0f * 4).roundToInt() shr 1) shl 1
                }
                _9_16 -> {
                    w = (parent.width shr 1) shl 1
                    h = ((w / 9.0f * 16).roundToInt() shr 1) shl 1
                }
            }
            ConstraintSet().apply {
                connect(viewId, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 0)
                connect(viewId, ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.LEFT, 0)
                connect(viewId, ConstraintSet.RIGHT, ConstraintSet.PARENT_ID, ConstraintSet.RIGHT, 0)
                connect(viewId, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 0)
                constrainWidth(viewId, w)
                constrainHeight(viewId, h)
            }.applyTo(parent)
            surfaceView.requestLayout()
        }
        btnCapture.setOnClickListener {
            onCaptureImage()
        }
    }

    private fun onCaptureImage() {
    }

    private fun startCamera() {
        val instance = ProcessCameraProvider.getInstance(this)
        instance.addListener(Runnable {
            val provider: ProcessCameraProvider = instance.get()
            //解绑之前的周期
            provider.unbindAll()

            //设置相机参数
            val selector: CameraSelector = CameraSelector.Builder()
                    .requireLensFacing(lensFacing)
                    .build()

            //构建预览
            val preview: Preview = Preview.Builder().build()

            //拍照用
            val imageCaptureBuilder = ImageCapture.Builder()
            imageCaptureBuilder.apply {
                setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
                setFlashMode(ImageCapture.FLASH_MODE_AUTO)
            }
            imageCapture = imageCaptureBuilder.build()
            // val hdrImageCaptureExtender = HdrImageCaptureExtender.create(imageCaptureBuilder)
            // hdrImageCaptureExtender.enableExtension(selector)

            //绑定相机
            val camera: Camera = provider.bindToLifecycle(this@DemoActivity, selector, preview, imageCapture)
            glThread!!.setCameraDegree(camera.cameraInfo.sensorRotationDegrees)
            preview.setSurfaceProvider(glThread!!, Preview.SurfaceProvider { request ->
                val resolution = request.resolution
                if (surface == null) {
                    glThread!!.apply {
                        val previewSize: Size = PreviewUtils.getPreviewSize(surfaceView, resolution)
                        setCameraSize(previewSize.width, previewSize.height)
                        surfaceTexture = SurfaceTexture(getOesTextureId()).apply {
                            setDefaultBufferSize(resolution.width, resolution.height)
                            setOnFrameAvailableListener(glThread, glThread!!.getHandler())
                        }
                    }
                    surface = Surface(surfaceTexture)
                }
                request.provideSurface(surface!!, glThread!!, Consumer {
                    println("provideSurface result " + Thread.currentThread().id)
                })
            })

        }, ContextCompat.getMainExecutor(this))

    }

    override fun onDestroy() {
        super.onDestroy()
        glThread?.apply {
            quitSafely()
        }
        glThread = null
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CameraXActivity.PERMISSION_REQUEST_CODE) {
            if (allPermissionsGranted()) {
                startCamera()
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