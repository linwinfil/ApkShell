package com.maoxin.app.login

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.databinding.DataBindingUtil
import com.maoxin.app.R
import com.maoxin.app.base.BaseViewModelActivity
import com.maoxin.app.databinding.ActivityLoginBinding

class LoginActivity : BaseViewModelActivity<LoginViewModel>() {

    private lateinit var mBtnLogin: Button
    private lateinit var mLoginBinding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun initData() {
        super.initData()
        mLoginBinding.loginModel = viewModel
    }

    override fun initView() {
        mLoginBinding = DataBindingUtil.setContentView(this, R.layout.activity_login)

        mBtnLogin = findViewById(R.id.btn_login)
        mBtnLogin.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
            }

        })
    }

    override fun getViewModelProvider(): Class<LoginViewModel>? {
        return LoginViewModel::class.java
    }
}
