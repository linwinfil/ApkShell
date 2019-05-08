package com.example.opengl.gl.filter;

import android.content.Context;
import android.text.TextUtils;

import com.example.opengl.gl.utils.GlUtils;

/**
 * @author lmx
 * Created by lmx on 2019/5/8.
 */
public abstract class AFilter
{

    protected Context mContext;

    protected final String mVertexShader;
    protected final String mFragmentShader;

    //执行句柄
    protected int mProgramHandle;
    //顶点坐标句柄
    protected int mPositionHandle;
    protected int mCoordinateHandle;

    public AFilter(Context mContext, String mVertexShader, String mFragmentShader)
    {
        this.mContext = mContext;
        this.mVertexShader = mVertexShader;
        this.mFragmentShader = mFragmentShader;
    }


    public void initProgramHandle() {
        if (!TextUtils.isEmpty(mVertexShader) && !TextUtils.isEmpty(mFragmentShader))
        {
            mProgramHandle = GlUtils.loadProgram(mVertexShader, mFragmentShader);
        }
    }


}
