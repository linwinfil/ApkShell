package com.maoxin.app.base

import android.os.Bundle
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.maoxin.app.MyApplication.Companion.showToast
import com.maoxin.app.data.ServerException
import kotlinx.coroutines.TimeoutCancellationException
import org.json.JSONException
import retrofit2.HttpException
import java.net.ConnectException
import java.net.UnknownHostException

/** @author lmx
 * Created by lmx on 2020/3/4.
 */
abstract class BaseViewModelActivity<VModel : BaseViewModel> : BaseActivity() {
    protected lateinit var viewModel: VModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun initData() {
        //let 内部执行后返回
        getViewModelProvider()?.let {
            viewModel = ViewModelProvider(this).get(it)
            lifecycle.addObserver(viewModel)
        }

        viewModel.also {
            it.getError().observe(this@BaseViewModelActivity, errorObserve)
            it.getFinally().observe(this@BaseViewModelActivity, finallyObserve)
        }
    }

    protected val errorObserve by lazy {
        Observer<Exception> {
            requestError(it)
        }
    }
    protected val finallyObserve by lazy {
        Observer<Int> {
            requestFinally(it)
        }
    }

    open fun requestError(it: Exception?) {
        it?.run {
            when (it) {
                is TimeoutCancellationException -> showToast("请求超时")
                is BaseRepository.ParamsException -> showToast("登陆超时")
                is UnknownHostException -> showToast("没有网络")
                is HttpException -> showToast("网络错误")
                is JSONException -> showToast("解析错误")
                is ConnectException -> showToast("连接失败")
                is ServerException -> showToast(it.message.toString())
            }
        }
    }

    open fun requestFinally(it: Int) {

    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.getError().removeObserver(errorObserve)
        viewModel.getFinally().removeObserver(finallyObserve)
        lifecycle.removeObserver(viewModel)
    }

    open fun getViewModelProvider(): Class<VModel>? = null
}