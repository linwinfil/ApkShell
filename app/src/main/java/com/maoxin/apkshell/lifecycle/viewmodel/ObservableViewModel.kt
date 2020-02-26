package com.maoxin.apkshell.lifecycle.viewmodel

import androidx.databinding.Observable
import androidx.databinding.PropertyChangeRegistry
import androidx.lifecycle.ViewModel

/** @author lmx
 * Created by lmx on 2020/2/26.
 */
open class ObservableViewModel : ViewModel(), Observable {

    private val callbacks: PropertyChangeRegistry = PropertyChangeRegistry()

    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
        callbacks.remove(callback)
    }

    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
        callbacks.add(callback)
    }

    fun onNotifyChange() = callbacks.notifyCallbacks(this, 0, null)

    fun onNotifyPropertyChanged(fieldId: Int) = callbacks.notifyCallbacks(this, fieldId, null)
}