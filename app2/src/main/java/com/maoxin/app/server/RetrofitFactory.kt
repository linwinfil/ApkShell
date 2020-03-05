package com.maoxin.app.server

import android.util.Log
import com.maoxin.app.data.LoginData
import com.maoxin.app.data.ResponseData
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import java.util.concurrent.TimeUnit

/** @author lmx
 * Created by lmx on 2020/3/4.
 */
object RetrofitFactory {
    val TAG: String = RetrofitFactory::class.java.simpleName

    val reqApi: RequestService by lazy {
        return@lazy Retrofit.Builder()
                .baseUrl(RequestService.BaseUrl)
                .client(getClient())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(RequestService::class.java)
    }


    private fun getClient(): OkHttpClient {
        return OkHttpClient().newBuilder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(180, TimeUnit.SECONDS)
                .writeTimeout(180, TimeUnit.SECONDS)
                .addInterceptor(getLogInterceptor())
                .retryOnConnectionFailure(true)//设置重连
                .build()
    }

    private fun getLogInterceptor(): Interceptor {
        val httpLoggingInterceptor = HttpLoggingInterceptor(logger = object : HttpLoggingInterceptor.Logger {
            override fun log(message: String) {
                Log.i(TAG, message)
            }
        })
        httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        return httpLoggingInterceptor
    }

    interface RequestService {

        companion object {
            val BaseUrl: String
                get() = "https://www.wanandroid.com/"
        }

        /**
         * https://www.wanandroid.com/user/login
         */
        @FormUrlEncoded
        @POST("user/login")
        suspend fun login(@Field("username") username: String, @Field("password") password: String): ResponseData<LoginData>
    }
}