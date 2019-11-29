package com.maoxin.apkshell.kotlin.example.recyclerview

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.maoxin.apkshell.R
import org.jetbrains.anko.backgroundColor
import kotlin.random.Random

class MainKotlinRecyclerViewActivity : AppCompatActivity() {

    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mAdapter: MyAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_kotlin_recycler_view)

        mRecyclerView = findViewById(R.id.m_recycler_view)
        val linearLayoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        mRecyclerView.layoutManager = linearLayoutManager

        mAdapter = MyAdapter()
        mRecyclerView.adapter = mAdapter

        Thread(Runnable {
            val list = ArrayList<VData>()
            val random = Random
            for (i in 1..256) {
                list.add(VData(Color.rgb(random.nextInt(256), random.nextInt(256), random.nextInt(256))))
            }

            runOnUiThread {
                mAdapter.list = list
                mAdapter.notifyDataSetChanged()
            }
        }).start()
    }

    inner class MyAdapter : RecyclerView.Adapter<VH>() {

        var list: ArrayList<VData>? = null

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
            val inflate = LayoutInflater.from(parent.context).inflate(R.layout.item_recycler_view, parent, false)
            return VH(inflate)
        }

        override fun getItemCount(): Int {
            return list?.size ?: 0
        }

        override fun onBindViewHolder(holder: VH, position: Int) {
            holder.itemView.setOnClickListener(mOnClickListener)
            list?.apply {
                holder.textView.text = (position + 1).toString()
                holder.itemView.backgroundColor = this[position].color
            }
        }


        private val mOnClickListener = View.OnClickListener { }
    }

    inner class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var textView: TextView = itemView.findViewById(R.id.item_txt)
    }

    inner class VData(var color: Int)

}
