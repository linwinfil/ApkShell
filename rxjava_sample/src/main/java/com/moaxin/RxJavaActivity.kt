package com.moaxin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.functions.Consumer
import my.beautycamera.R
import java.util.jar.Manifest

class RxJavaActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rx_java)

        runOnUiThread {
            RxPermissions(this)
                    .request(android.Manifest.permission.CAMERA)
                    .subscribe { grant ->
                        if (grant) {
                            Toast.makeText(this@RxJavaActivity, "grant", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this@RxJavaActivity, "not", Toast.LENGTH_SHORT).show()
                        }
                    }
        }
    }
}