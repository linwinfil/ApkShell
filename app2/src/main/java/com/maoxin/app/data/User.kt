package com.maoxin.app.data

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id

/** @author lmx
 * Created by lmx on 2020/8/16.
 */
@Entity
data class User(
        @Id
        var id: Long = 0,
        var name: String? = null,
        var pwd: String? = null
)