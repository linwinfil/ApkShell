package com.maoxin.apkshell.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.maoxin.apkshell.R

class MainClassLoaderActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_class_loader)


        val data: ArrayList<Colder> = ArrayList()
        data.add(Colder("m6t[eiqczKtZmgK8nFz_zVbc"))
        data.add(Colder("RsyWu[cin[zICddpqXifq]K:iamfxWAoMHe^cee\\r`s`SxrbVStaqcyMWDC;"))
        data.add(Colder("zGkeGwCovSvMgZrbcft[zTI8BzVCw`w`inXEj\\xWt^tYS@dqcohdChuYLoDsCguPcan-pSRzBcs_cpRwfnNKiljjlZL9KC"))
        data.add(Colder("L?"))
        data.add(Colder("jWcmu[XamW"))
        data.add(Colder("zIuSrObleiadEqBKbb"))
        data.add(Colder("FyOsn]ZpsRt^cpCllczTij"))
        data.add(Colder("Ltu_ikgix["))


        // data.add(Colder())

        data.forEach(action = {
            println(it.P())
        })
    }

    data class Colder(val key: String) {

        var value: String? = null

        fun C(): String? {
            value = com.analysys.expect.Cold._var_41(key)
            return value
        }

        public fun P() {
            val c = C()
            println("key={$key}, value=${c}")
        }
    }
}
