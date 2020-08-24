package com.maoxin.apkshell.activity

import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.os.Debug
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.maoxin.apkshell.R
import com.maoxin.apkshell.hook.ams.AMSHookHelper
import com.maoxin.apkshell.ipc.client.ClientActivity
import com.maoxin.apkshell.kotlin.example.recyclerview.MainKotlinRecyclerViewActivity
import com.maoxin.apkshell.lifecycle.activity.ViewModelMainActivity

class MainOPKotlinActivity : AppCompatActivity() {

    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mGridAdapter: GridAdapter
    private var mList: ArrayList<VHData> = ArrayList()

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(newBase)

        AMSHookHelper.hookAMS()
        AMSHookHelper.hookActivityThread()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_op_kotlin)


        Debug.startMethodTracing()

        mRecyclerView = findViewById(R.id.m_recycler_view)
        val gridLayoutManager = GridLayoutManager(this, 4)
        mRecyclerView.layoutManager = gridLayoutManager

        val gridItemDecoration = GridItemDecoration(4, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2f, this.resources.displayMetrics).toInt())
        mRecyclerView.addItemDecoration(gridItemDecoration)

        mGridAdapter = GridAdapter()
        mRecyclerView.adapter = mGridAdapter

        Debug.stopMethodTracing()
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        mList.add(VHData("test", null))
        mList.add(VHData("权限", Main4Activity::class.java))
        mList.add(VHData("mp4_parser", Main5Activity::class.java))
        mList.add(VHData("mp4_parser_ex", Main5ExActivity::class.java))
        mList.add(VHData("美颜美型工具", MainShapeUtilsActivity::class.java))
        mList.add(VHData("并发测试", Main8Activity::class.java))
        mList.add(VHData("21协议调用", Main9Activity::class.java))
        mList.add(VHData("21_2.0.0_脸型数据的转换", Main10Activity::class.java))
        mList.add(VHData("Kotlin", MainKotlinRecyclerViewActivity::class.java))
        mList.add(VHData("TaskStackBuilder返回栈", Main12Activity::class.java))
        mList.add(VHData("打开CameraX", MainCameraXActivity::class.java))
        mList.add(VHData("事件拦截", Main2Activity::class.java))
        mList.add(VHData("Android 10 储存", Main13KotlinActivity::class.java))
        mList.add(VHData("binder ipc test", ClientActivity::class.java))
        mList.add(VHData("Video Model test", ViewModelMainActivity::class.java))
        mList.add(VHData("易观CL", MainClassLoaderActivity::class.java))
        mList.add(VHData("Hook Activity", MainHookActivity::class.java))
        mList.add(VHData("Hook Activity4Java", MainHook4JActivity::class.java))
        mList.add(VHData("Toast BadWindowToken问题", MainToastActivity::class.java))
        mList.add(VHData("Canvas 测试", MainCanvasActivity::class.java))
        mList.add(VHData("解析", MainParseActivity::class.java));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            mList.add(VHData("Image Decoder", MainImageDecoderActivity::class.java.javaClass))
        }

        mGridAdapter.notifyDataSetChanged()
    }

    class GridItemDecoration(private val spanCount: Int, private val spacing: Int) : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
            val position = parent.getChildAdapterPosition(view)
            val column = position % spanCount

            outRect.left = column * spacing / spanCount // column * ((1f / spanCount) * spacing)
            outRect.right = spacing - (column + 1) * spacing / spanCount // spacing - (column + 1) * ((1f /    spanCount) * spacing)

            if (position >= spanCount) {
                outRect.top = spacing // item top
            }
        }
    }

    private val mOnClickListener = View.OnClickListener { v ->
        val vhData: VHData? = v?.tag as VHData
        vhData?.also {
            it.activity?.apply {
                startActivity(Intent(this@MainOPKotlinActivity, this))
            }
        }
    }


    inner class GridAdapter : RecyclerView.Adapter<GridVH>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GridVH {
            val inflate = LayoutInflater.from(parent.context).inflate(R.layout.item_op_recycler_view, null, false)
            return GridVH(inflate)
        }

        override fun getItemCount(): Int {
            return mList.size
        }

        override fun onBindViewHolder(holder: GridVH, position: Int) {
            val vhData = mList[position]
            holder.button.tag = vhData
            holder.button.text = vhData.text
            holder.button.setOnClickListener(this@MainOPKotlinActivity.mOnClickListener)
        }


    }

    inner class GridVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var button: Button = itemView.findViewById(R.id.m_item_button_view)
    }

    private data class VHData(val text: String, val activity: Class<*>?)
}
