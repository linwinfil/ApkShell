package com.maoxin.app.box

import androidx.annotation.NonNull
import com.maoxin.app.box.MyObjectBox.boxStore
import io.objectbox.Box
import io.objectbox.query.QueryBuilder

/** @author lmx
 * Created by lmx on 2020/9/16.
 */
class BoxDao private constructor() {
    companion object {
        val instance: BoxDao by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            BoxDao()
        }
    }

    /**
     * 获取列表
     *
     * @return java.util.List<T>
     * @params [tClass]
    </T> */
    fun <T> getList(@NonNull tClass: Class<T>): List<T>? {
        val list: MutableList<T> = ArrayList()
        val box: Box<T> = boxStore.boxFor(tClass)
        list.addAll(box.query().build().find())
        return list
    }

    fun <T> replace(@NonNull tClass:Class<T>) {
        val box:Box<T> = boxStore.boxFor(tClass)
        val query:QueryBuilder<T> = box.query()
    }


    /**
     * 保存一个数据
     *
     * @return void
     * @params [clazz, value]
     */
    fun <T> putData(@NonNull clazz: Class<T>, value: T) {
        val box = boxStore.boxFor(clazz)
        box.put(value)
    }


}