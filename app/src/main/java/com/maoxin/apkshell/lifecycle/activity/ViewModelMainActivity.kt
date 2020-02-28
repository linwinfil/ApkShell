package com.maoxin.apkshell.lifecycle.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatEditText
import androidx.databinding.*
import androidx.lifecycle.ViewModelProviders
import com.maoxin.apkshell.R
import com.maoxin.apkshell.databinding.ActivityViewModelMainBinding
import com.maoxin.apkshell.lifecycle.viewmodel.MainViewModel


/**
 * 1、单向绑定：由数据驱动到View层更新
 * 2、双向绑定：View层数据更新触发model层更新
 */
class ViewModelMainActivity : AppCompatActivity(), MainViewModel.Handlers {
    companion object {
        private val TAG: String = "ViewModelMainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DataBindingUtil.setContentView<ActivityViewModelMainBinding>(this, R.layout.activity_view_model_main).also {
            it.viewModel = ViewModelProviders.of(this@ViewModelMainActivity)[MainViewModel::class.java].apply {
                this.text = "view model 测试文本"
            }
            it.handlers = this@ViewModelMainActivity
        }
    }

    @BindingAdapter("onInverseBindingEvent")
    fun onInverseBindingEventListener(eventListener: InverseBindingListener?) {
        eventListener?.onChange()
    }


    override fun onNatigateToOtherActivityClick(view: View) {
        onStartActivity<ViewModelMain2Activity>((view as TextView).text.toString())
    }

    private inline fun <reified T : Activity> onStartActivity(args: String?) {
        val intent = Intent(this, T::class.java)
        intent.putExtra("text", args)
        this@ViewModelMainActivity.startActivity(intent)
    }

    @InverseBindingMethods(value = [InverseBindingMethod(type = EditTextExtend::class, attribute = "android:text"/*, event = "updateInputText"*/)])
    open class EditTextExtend : AppCompatEditText {
        constructor(context: Context?) : super(context) {
            init()
        }

        constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
            init()
        }

        constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
            init()
        }

        private fun init() {

        }

        fun setUpdateInputText(inverseBindingListener: InverseBindingListener?) {
            inverseBindingListener?.onChange() ?: Log.d(TAG, "inverseBindingListener 为空")
        }
    }
}
