package com.maoxin.app.base

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider

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
    }

    override fun onDestroy() {
        super.onDestroy()
        lifecycle.removeObserver(viewModel)
    }

    open fun getViewModelProvider(): Class<VModel>? = null
}