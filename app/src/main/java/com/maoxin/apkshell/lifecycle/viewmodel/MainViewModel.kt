package com.maoxin.apkshell.lifecycle.viewmodel

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.databinding.library.baseAdapters.BR

/** @author lmx
 * Created by lmx on 2020/2/26.
 */
class MainViewModel : BaseObservable() {


    @get:Bindable
    var text = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.text) //通知UI刷新
        }

    @get:Bindable
    var inputNumber: String = "0"
        set(value) {
            field = value
            notifyPropertyChanged(BR.inputNumber)
        }

    companion object {
        open class SimpleTextWatcher : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        }
    }


    interface Handlers {
        fun onNatigateToOtherActivityClick(view: View)
    }

}