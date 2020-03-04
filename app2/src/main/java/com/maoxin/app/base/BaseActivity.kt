package com.maoxin.app.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

/** @author lmx
 * Created by lmx on 2020/3/4.
 */
abstract class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
        initData()
    }

    protected abstract fun initData()
    protected abstract fun initView()
}