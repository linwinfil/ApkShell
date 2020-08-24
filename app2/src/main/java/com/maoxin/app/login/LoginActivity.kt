package com.maoxin.app.login

import android.annotation.SuppressLint
import android.content.Context
import android.database.Cursor
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.autofill.AutofillManager
import android.widget.Button
import android.widget.CursorAdapter
import android.widget.EditText
import androidx.databinding.BindingAdapter
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.maoxin.app.MyApplication.Companion.showToast
import com.maoxin.app.R
import com.maoxin.app.base.BaseViewModelActivity
import com.maoxin.app.box.Objectbox
import com.maoxin.app.data.LoginData
import com.maoxin.app.data.ResponseData
import com.maoxin.app.data.User
import com.maoxin.app.databinding.ActivityLoginBinding
import com.maoxin.app.utils.CommonUtils
import com.maoxin.app.utils.SharedPreUtils
import java.io.File
import java.lang.ref.WeakReference

class LoginActivity : BaseViewModelActivity<LoginViewModel>() {
    companion object {

        @JvmStatic
        @BindingAdapter("android:text")
        fun setText(view: EditText, text: CharSequence) {
            val old: String = view.text.toString()
            val toString = text.toString()
            if (old == toString) {
                return
            }
            view.setText(toString)
        }
    }

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

        viewModel.also {
            if (SharedPreUtils.contain("username")) {
                it.username = SharedPreUtils.get("username", "") as String
            }
            if (SharedPreUtils.contain("password")) {
                it.password = SharedPreUtils.get("password", "") as String
            }
        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mAutoFillManager = this.getSystemService(AutofillManager::class.java) as AutofillManager
        }
    }

    private val loginObserve by lazy {
        Observer<ResponseData<LoginData>> {
            when (it.errorCode) {
                0 -> {
                    val user = User(1, viewModel.username, CommonUtils.base64Encode(viewModel.password))
                    Objectbox.boxStore.boxFor(User::class.java).put(user)
                    SharedPreUtils.save("username", user.name!!)
                    SharedPreUtils.save("password", user.pwd!!)
                    showToast("登录成功")

                    val externalFilesDir = this@LoginActivity
                            .getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
                    externalFilesDir?.also { file ->
                        if (file.exists()) {
                            val file1 = File(file.absolutePath, "login.data")
                            file1.createNewFile()
                            println("login.data exist: ${file1.exists()}")
                        }
                    }
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

    private val scanMediaTask: AsyncTask<WeakReference<Context>, Void?, Void?> =
            @SuppressLint("StaticFieldLeak")
            object : AsyncTask<WeakReference<Context>, Void?, Void?>() {
                override fun doInBackground(vararg params: WeakReference<Context>?): Void? {
                    val context: Context? = params[0]?.get()
                    context?.also {
                        val mediaColumns = arrayOf(
                                MediaStore.Video.Media._ID, MediaStore.Video.Media.DATA,
                                MediaStore.Video.Media.TITLE, MediaStore.Video.Media.MIME_TYPE,
                                MediaStore.Video.Media.DISPLAY_NAME, MediaStore.Video.Media.SIZE,
                                MediaStore.Video.Media.DATE_ADDED, MediaStore.Video.Media.DURATION,
                                MediaStore.Video.Media.WIDTH, MediaStore.Video.Media.HEIGHT)

                        val cursor: Cursor? = it.contentResolver.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                                mediaColumns, null, null,
                                MediaStore.Video.Media.DATE_ADDED)

                    }
                    return null
                }

            }

    override fun getViewModelProvider(): Class<LoginViewModel>? {
        return LoginViewModel::class.java
    }
}
