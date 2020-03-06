package com.maoxin.app.base

import androidx.databinding.Observable
import androidx.databinding.PropertyChangeRegistry
import androidx.lifecycle.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout


/** @author lmx
 * Created by lmx on 2020/3/4.
 */
open class BaseViewModel : ViewModel(), LifecycleObserver, Observable {
    @Transient
    private var callbacks: PropertyChangeRegistry? = null
    private val error by lazy { MutableLiveData<Exception>() }
    private val finally by lazy { MutableLiveData<Int>() }

    /**
     * 请求失败
     */
    fun getError(): LiveData<Exception> {
        return error
    }

    /**
     * 请求完成
     */
    fun getFinally(): LiveData<Int> {
        return finally
    }


    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel()
        callbacks?.clear()
    }

    fun launchUI(block: suspend CoroutineScope.() -> Unit) = viewModelScope.launch {
        try {
            withTimeout(5_000) {//5s超时抛异常
                block()
            }
        } catch (e: Exception) {
            error.value = e
        } finally {
            finally.value = 200
        }
    }

    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
        synchronized(this) {
            callbacks?.remove(callback) ?: return
        }
    }

    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
        synchronized(this) {
            callbacks ?: PropertyChangeRegistry()
        }
        callbacks?.add(callback)
    }


    open fun notifyChange() {
        synchronized(this) {
            callbacks?.notifyCallbacks(this, 0, null) ?: return
        }
    }

    open fun notifyPropertyChanged(fieldId: Int) {
        synchronized(this) {
            callbacks?.notifyCallbacks(this, fieldId, null) ?: return
        }
    }
}