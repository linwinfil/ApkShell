package com.maoxin.apkshell.lifecycle.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.maoxin.apkshell.R
import com.maoxin.apkshell.activity.MainOPActivity
import com.maoxin.apkshell.databinding.ActivityViewModelMainBinding
import com.maoxin.apkshell.lifecycle.viewmodel.MainViewModel

class ViewModelMainActivity : AppCompatActivity(), MainViewModel.Handlers {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // setContentView(R.layout.activity_view_model_main)
        DataBindingUtil.setContentView<ActivityViewModelMainBinding>(this, R.layout.activity_view_model_main).also {
            val viewModelProvider = ViewModelProvider(this)
            it.viewModel = viewModelProvider[MainViewModel::class.java].apply {
                text = "view model 测试文本"
            }
            it.handlers = this
        }
    }

    override fun onNatigateToOtherActivityClick(view: View) {
        startActivity<MainOPActivity>()
    }

    private inline fun <reified T : Activity> startActivity() {
        startActivity(Intent(this, T::class.java))
    }
}
