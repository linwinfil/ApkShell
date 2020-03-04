package com.maoxin.apkshell.lifecycle.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.BindingAdapter
import androidx.databinding.DataBindingUtil
import com.maoxin.apkshell.R
import com.maoxin.apkshell.databinding.ActivityViewModelMainBinding
import com.maoxin.apkshell.lifecycle.ui.EditTextExtend
import com.maoxin.apkshell.lifecycle.viewmodel.MainViewModel


/**
 * 1、单向绑定：由数据驱动将值更新到View层
 * 2、双向绑定：View层的数据更新将触发model层更新<br/>
 *
 */
class ViewModelMainActivity : AppCompatActivity(), MainViewModel.Handlers {
    companion object {
        private val TAG: String = "ViewModelMainActivity"

        @JvmStatic
        @BindingAdapter("android:text")
        fun setText(view: EditText, text: CharSequence) {
            val old: String = view.text.toString()
            val toString = text.toString()
            if (old == toString) {
                return
            }

            view.setText(toString)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val activityViewModelMainBinding: ActivityViewModelMainBinding = DataBindingUtil.setContentView<ActivityViewModelMainBinding>(this, R.layout.activity_view_model_main)
        activityViewModelMainBinding.also {
            it.viewModel = MainViewModel().apply {
                this.text = "view model 测试文本"
            }
            it.handlers = this@ViewModelMainActivity

            it.editText.setInputTextChangeListener(object : EditTextExtend.OnInputTextChangeListener {
                override fun onInputTextChange(arg: String) {
                    val value: Int = arg.toInt()
                    it.textView.isClickable = if (value > 0) {
                        it.textView.text = "数字有效"
                        true
                    } else {
                        it.textView.text = "数字无效"
                        false
                    }

                    it.viewModel?.inputNumber2 = arg
                }
            })
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
