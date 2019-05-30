package com.maoxin.apkshell.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.maoxin.apkshell.R

class CameraXFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_camera_x, container, false)
    }

    override fun onResume() {
        super.onResume()

        view?.let {
            it.postDelayed({
                it.systemUiVisibility = com.maoxin.apkshell.activity.FLAG_FULLSCREEN
            }, 500)
        }
    }
}
