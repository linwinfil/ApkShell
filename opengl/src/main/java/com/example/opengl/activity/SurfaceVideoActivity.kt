package com.example.opengl.activity

import android.app.Activity
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.opengl.R

class SurfaceVideoActivity : AppCompatActivity(), MediaPlayer.OnPreparedListener {

    var mediaPlayer: MediaPlayer? = null

    lateinit var surfaceView: SurfaceView
    lateinit var surfaceHolder: SurfaceHolder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_surface_video)

        findViewById<Button>(R.id.btn_open_video).setOnClickListener {
            val photoPickerIntent = Intent(Intent.ACTION_PICK).apply {
                type = "video/*"
            }
            this.startActivityForResult(photoPickerIntent, 0x22)
        }

        surfaceView = findViewById(R.id.surface_view)
        val holder = surfaceView.holder
        holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceChanged(h: SurfaceHolder?, p1: Int, p2: Int, p3: Int) {
                println("surfaceChanged")
            }

            override fun surfaceDestroyed(h: SurfaceHolder?) {
                println("surfaceDestroyed")
            }

            override fun surfaceCreated(h: SurfaceHolder?) {
                if (h != null) {
                    surfaceHolder = h
                }

            }

        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 0x22 && resultCode == Activity.RESULT_OK) {
            Handler(Looper.getMainLooper()).postDelayed({
                data?.data?.also {
                    mediaPlayer?.apply {
                        stop()
                        release()
                        setOnPreparedListener(null)
                    }
                    mediaPlayer = MediaPlayer()
                    mediaPlayer?.also { md ->
                        md.setDataSource(this, it)
                        md.setDisplay(surfaceHolder)
                        md.setOnPreparedListener(this)
                        md.isLooping = true
                        md.prepareAsync()
                    }
                }
            }, 1000)
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onPrepared(player: MediaPlayer?) {
        player?.start()
    }
}