package com.maoxin.apkshell.activity

import android.annotation.TargetApi
import android.graphics.Canvas
import android.graphics.ImageDecoder
import android.graphics.PixelFormat
import android.graphics.PostProcessor
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.annotation.RequiresApi
import com.maoxin.apkshell.R
import java.io.File

@RequiresApi(Build.VERSION_CODES.P)
class MainImageDecoderActivity : AppCompatActivity() {


    companion object {
        val pics: Array<String> = arrayOf("a-gif.gif", "aaa.jpg", "b-gif.gif", "c-gif.gif", "timg.jpg")
    }


    val imgView: ImageView by lazy {
        findViewById<ImageView>(R.id.imgv_pic)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_image_decoder)


        findViewById<Button>(R.id.btn_image_decoder).setOnClickListener {
            decoder()
        }

    }

    private fun decoder() {
        val picPath = pics.random()


        val source = ImageDecoder.createSource(File(""))
        val decodeDrawable = ImageDecoder.decodeDrawable(source, object : ImageDecoder.OnHeaderDecodedListener {
            override fun onHeaderDecoded(decoder: ImageDecoder,
                                         info: ImageDecoder.ImageInfo,
                                         source: ImageDecoder.Source) {
                //解码之前 参数设置的操作
                decoder.setTargetSampleSize(2)

                //默认解码出来bitmap是不可变的，可以通过PostProcessor添加一些自定义效果
                decoder.setPostProcessor(object : PostProcessor {
                    override fun onPostProcess(canvas: Canvas): Int {

                        return PixelFormat.TRANSLUCENT
                    }

                })
            }
        })

        decodeDrawable
    }
}
