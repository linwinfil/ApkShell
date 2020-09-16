package com.maoxin.app.data

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id

/** @author lmx
 * Created by lmx on 2020/9/16.
 */
@Entity
data class Card(
        @Id(assignable = true)
        var id: Long = 0,
        var no:Long = 0
)