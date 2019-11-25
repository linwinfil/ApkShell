package com.maoxin.apkshell.fragment

import android.content.Context
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Rational
import android.util.Size
import android.view.LayoutInflater
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import androidx.camera.core.AspectRatio
import androidx.camera.core.CameraX
import androidx.camera.core.Preview
import androidx.camera.core.PreviewConfig
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.maoxin.apkshell.R
import java.io.File

class CameraXFragment : Fragment() {


    companion object {
        fun GetOutputDirectory(context: Context): File {
            val applicationContext = context.applicationContext
            val mediaDir = context.externalMediaDirs.firstOrNull()?.let {
                File(it, applicationContext.resources.getString(R.string.app_name)).apply {
                    mkdirs()
                }
            }
            return if (mediaDir != null && mediaDir.exists()) mediaDir else applicationContext.filesDir
        }
    }

    private lateinit var outputDirectory: File

    private lateinit var container: ConstraintLayout
    private lateinit var textureView: TextureView


    private var lensFacing = CameraX.LensFacing.BACK


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_camera_x, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        container = view as ConstraintLayout
        textureView = container.findViewById(R.id.textureView)

        outputDirectory = GetOutputDirectory(requireContext())

        textureView.postDelayed({


        }, 600)

    }

    override fun onResume() {
        super.onResume()

        //作用域函数[https://juejin.im/post/5ac03b57f265da238532ffa4]
        var a = true
        if (a) {
            view?.let {
                it.postDelayed({
                    it.systemUiVisibility = com.maoxin.apkshell.activity.FLAG_FULLSCREEN
                }, 500)
            }
        } else {
            view?.run {
                postDelayed({
                    this.systemUiVisibility = com.maoxin.apkshell.activity.FLAG_FULLSCREEN
                }, 500)
            }

            view?.also {
                it.postDelayed({
                    it.systemUiVisibility = com.maoxin.apkshell.activity.FLAG_FULLSCREEN
                }, 500)
            }
        }


    }

    private fun bindCameraXUserCase() {
        CameraX.unbindAll()


    }


    private fun createPreviewUserCase(): Preview {
        val previewConfig = createPreviewConfig()


        return Preview(previewConfig)
    }

    private fun createPreviewConfig(): PreviewConfig {

        val metrics = DisplayMetrics().apply {
            textureView.display.getRealMetrics(this)
        }
        val size = Size(metrics.widthPixels, metrics.heightPixels)
        val rational = Rational(metrics.widthPixels, metrics.heightPixels)
        val rotation = textureView.display.rotation


        return PreviewConfig.Builder().apply {
            setLensFacing(lensFacing)
            setTargetResolution(size)
            setTargetRotation(rotation)
            setTargetAspectRatio(AspectRatio.RATIO_16_9)
        }.build()
    }

}
