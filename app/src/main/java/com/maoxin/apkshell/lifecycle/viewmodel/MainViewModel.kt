package com.maoxin.apkshell.lifecycle.viewmodel

import android.view.View
import androidx.databinding.Bindable
import androidx.databinding.library.baseAdapters.BR

/** @author lmx
 * Created by lmx on 2020/2/26.
 */
class MainViewModel : ObservableViewModel() {


    @get:Bindable
    var text = ""
        set(value) {
            field = value
            onNotifyPropertyChanged(BR.text)
        }

    @Bindable
    var inputNumber: String = 0.toString()
        set(value) {
            if (field != value) {
                field = value
                onNotifyPropertyChanged(BR.inputNumber)
            }
        }
        get() {
            return field
        }

    interface Handlers {
        fun onNatigateToOtherActivityClick(view: View)
    }

}