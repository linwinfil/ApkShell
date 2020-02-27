package com.maoxin.apkshell.lifecycle.activity

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.maoxin.apkshell.R
import com.maoxin.apkshell.databinding.ActivityViewModelMain2Binding
import com.maoxin.apkshell.lifecycle.viewmodel.MainViewModel

class ViewModelMain2Activity : AppCompatActivity(), MainViewModel.Handlers {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val getText = if (intent?.hasExtra("text") == true) {
            intent.getStringExtra("text")
        } else {
            ""
        }

        DataBindingUtil.setContentView<ActivityViewModelMain2Binding>(this, R.layout.activity_view_model_main2).also {
            it.viewmodel = ViewModelProviders.of(this@ViewModelMain2Activity)[MainViewModel::class.java].apply {
                this.text = getText
            }
        }
    }

    override fun onNatigateToOtherActivityClick(view: View) {
        finish()
    }
}
