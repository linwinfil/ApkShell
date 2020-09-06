package com.maoxin.apkshell.activity

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.camera.core.Preview
import com.maoxin.apkshell.R
import com.maoxin.apkshell.view.PreviewViewV2
import org.jetbrains.anko.contentView
import java.lang.ref.WeakReference

class MainPreviewView2Activity : AppCompatActivity() {

    lateinit var view: PreviewViewV2
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val inflate: ViewGroup = LayoutInflater.from(this).inflate(R.layout.activity_main_preview_view2, null, false)
                as ViewGroup
        setContentView(inflate)

        view = PreviewViewV2(this)
        val params = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
        inflate.addView(view, params)
    }

    override fun onResume() {
        super.onResume()
        loadImgTask.execute(WeakReference(this))
    }

    val loadImgTask: AsyncTask<WeakReference<Context>, Void?, Bitmap?> = @SuppressLint("StaticFieldLeak")
    object : AsyncTask<WeakReference<Context>, Void?, Bitmap?>() {
        override fun doInBackground(vararg params: WeakReference<Context>?): Bitmap? {
            val context = params[0]!!.get()
            context?.apply {
                val open = assets.open("IMG_20200611_232245.jpg")
                return BitmapFactory.decodeStream(open)
            }
            return null
        }

        override fun onPostExecute(result: Bitmap?) {
            this@MainPreviewView2Activity.view.setImage(result!!)
        }

    }
}