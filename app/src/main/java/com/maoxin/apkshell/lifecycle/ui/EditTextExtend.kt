package com.maoxin.apkshell.lifecycle.ui

import android.content.Context
import android.text.Editable
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import androidx.databinding.*
import com.maoxin.apkshell.lifecycle.viewmodel.MainViewModel

/** @author lmx
 * Created by lmx on 2020/3/2.
 * InverseBindingMethods<br/>
 *      attribute：指定支持逆向绑定的属性
 *      event：指定 valueChanged 监听事件
 *      method：指定逆向绑定的时候的数据来源方法
 */
@InverseBindingMethods(value = [InverseBindingMethod(type = EditTextExtend::class, attribute = "inputText", method = "getInputText", event = "inputTextAttrChanged")])
@BindingMethods(value = [BindingMethod(type = EditTextExtend::class, attribute = "inputText", method = "setInputText")])
open class EditTextExtend : AppCompatEditText {

    interface OnInputTextChangeListener {
        fun onInputTextChange(arg: String)
    }

    companion object {
        @JvmStatic
        @BindingAdapter("inputTextAttrChanged")
        fun onInputTextChanged(view: EditTextExtend?, bindingListener: InverseBindingListener?) {
            view?.listener?.onInputTextChange(view.inputText ?: "")
            bindingListener?.onChange()
        }


    }

    private var inputText: String? = null
    private var listener: OnInputTextChangeListener? = null

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
        addTextChangedListener(object : MainViewModel.Companion.SimpleTextWatcher() {
            override fun afterTextChanged(s: Editable?) {
                s?.toString()?.let { it ->
                    setIuputTextImpl(arg = (if (it.isEmpty()) 0.toString() else it))
                    onInputTextChanged(this@EditTextExtend, null)
                }
            }
        })
    }

    fun setInputTextChangeListener(l: OnInputTextChangeListener?) {
        listener = l
    }

    fun getInputText(): String {
        return text.toString()
    }

    fun setInputText(arg: String) {
        inputText = getInputText()
        if (inputText == arg/*==相当于equals*/) {
            return
        }
        setIuputTextImpl(arg)
        setText(arg)
    }

    private inline fun setIuputTextImpl(arg: String) {
        inputText = arg
    }


}
