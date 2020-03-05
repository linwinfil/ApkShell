package com.maoxin.app.data

/** @author lmx
 * Created by lmx on 2020/3/4.
 */
data class LoginData(val username: String,
                     val password: String,
                     val token: String,
                     val admin: Boolean,
                     val chapterTops: List<Any>,
                     val collectIds: List<String>,
                     val email: String,
                     val icon: String,
                     val nickname: String,
                     val publicName: String,
                     val type: Int)