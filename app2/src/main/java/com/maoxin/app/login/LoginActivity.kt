package com.maoxin.app.login

import android.os.Build
import android.os.Bundle
import android.view.autofill.AutofillManager
import android.widget.Button
import android.widget.EditText
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.maoxin.app.R
import com.maoxin.app.base.BaseViewModelActivity
import com.maoxin.app.data.LoginData
import com.maoxin.app.data.ResponseData
import com.maoxin.app.databinding.ActivityLoginBinding

class LoginActivity : BaseViewModelActivity<LoginViewModel>() {

    private lateinit var mLoginBinding: ActivityLoginBinding
    private var mAutoFillManager: AutofillManager? = null

    private val mBtnLogin: Button by lazy {
        findViewById<Button>(R.id.btn_login)
    }
    private val mEditUserName: EditText by lazy {
        findViewById<EditText>(R.id.edit_username)
    }
    private val mEditPassword: EditText by lazy {
        findViewById<EditText>(R.id.edit_password)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onDestroy() {
        viewModel.getLogin().removeObserver(loginObserve)
        super.onDestroy()
    }

    override fun initData() {
        super.initData()
        mLoginBinding.loginModel = viewModel
        viewModel.getLogin().observe(this@LoginActivity, loginObserve)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mAutoFillManager = this.getSystemService(AutofillManager::class.java) as AutofillManager
        }
    }

    private val loginObserve by lazy {
        Observer<ResponseData<LoginData>> {
            when (it.errorCode) {
                0 -> {
                    showToast("登录成功")
                }
                else -> {
                    showToast(it.errorMsg)
                }
            }
        }
    }

    override fun initView() {
        mLoginBinding = DataBindingUtil.setContentView(this, R.layout.activity_login)

        mBtnLogin.setOnClickListener {
            viewModel.also {
                val username = mEditUserName.text.toString()
                val password = mEditPassword.text.toString()
                viewModel.onLogin(username, password)
            }
        }
    }

    override fun getViewModelProvider(): Class<LoginViewModel>? {
        return LoginViewModel::class.java
    }
}
