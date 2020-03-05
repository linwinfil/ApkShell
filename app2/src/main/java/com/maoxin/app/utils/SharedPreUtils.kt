package com.maoxin.app.utils

import android.content.Context
import android.content.SharedPreferences

/** @author lmx
 * Created by lmx on 2020/3/5.
 */
object SharedPreUtils {

    private var mSp: SharedPreferences? = null

    fun init(context: Context, spname: String) {
        mSp = context.getSharedPreferences(spname, Context.MODE_PRIVATE)
    }

    fun save(key: String, value: Any) {
        val editor: SharedPreferences.Editor? = mSp?.edit()
        when (value) {
            is String -> {
                editor?.putString(key, value)
            }
            is Int -> {
                editor?.putInt(key, value)
            }
            is Boolean -> {
                editor?.putBoolean(key, value)
            }
            is Float -> {
                editor?.putFloat(key, value)
            }
            is Long -> {
                editor?.putLong(key, value)
            }
            else -> {
                editor?.putString(key, value.toString())
            }
        }
        editor?.apply()
    }


    fun get(key: String, defaultValue: Any): Any? {
        return when (defaultValue) {
            is String -> {
                mSp?.getString(key, defaultValue)
            }
            is Int -> {
                mSp?.getInt(key, defaultValue)
            }
            is Boolean -> {
                mSp?.getBoolean(key, defaultValue)
            }
            is Float -> {
                mSp?.getFloat(key, defaultValue)
            }
            is Long -> {
                mSp?.getLong(key, defaultValue)
            }
            else -> {
                null
            }
        }
    }


    fun remove(key: String) {
        val editor: SharedPreferences.Editor? = mSp?.edit()
        editor?.remove(key)?.apply()
    }


    fun clear() {
        val editor: SharedPreferences.Editor? = mSp?.edit()
        editor?.clear()?.apply()
    }


    fun contain(key: String): Boolean {
        return mSp?.contains(key) ?: false
    }
}