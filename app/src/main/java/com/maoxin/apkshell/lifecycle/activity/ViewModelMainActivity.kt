package com.maoxin.apkshell.lifecycle.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.maoxin.apkshell.R
import com.maoxin.apkshell.databinding.ActivityViewModelMainBinding
import com.maoxin.apkshell.lifecycle.viewmodel.MainViewModel

class ViewModelMainActivity : AppCompatActivity(), MainViewModel.Handlers {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DataBindingUtil.setContentView<ActivityViewModelMainBinding>(this, R.layout.activity_view_model_main).also {
            it.viewModel = ViewModelProviders.of(this@ViewModelMainActivity)[MainViewModel::class.java].apply {
                this.text = "view model 测试文本"
            }
            it.handlers = this@ViewModelMainActivity
        }
    }

    override fun onNatigateToOtherActivityClick(view: View) {
        onStartActivity<ViewModelMain2Activity>((view as TextView).text.toString())
    }

    private inline fun <reified T : Activity> onStartActivity(args: String?) {
        val intent = Intent(this, T::class.java)
        intent.putExtra("text", args)
        this@ViewModelMainActivity.startActivity(intent)
    }
}
