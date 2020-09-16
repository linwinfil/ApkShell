package com.maoxin.app.data

import io.objectbox.annotation.Backlink
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.annotation.NameInDb
import io.objectbox.relation.ToMany

/** @author lmx
 * Created by lmx on 2020/8/16.
 */
@Entity
data class User(
        @Id(assignable = true)
        var id: Long = 0,
        var userId: Long = 0,
        var name: String? = null,
        var pwd: String? = null,

        var msg: String? = null
) {
        @NameInDb("cards")
        lateinit var cards:ToMany<Card>
}