package com.example.opengl.gl.filter;

import android.content.Context;
import android.opengl.GLES20;
import android.util.Log;

import com.example.opengl.gl.utils.GlMatrixTools;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * @author lmx
 * Created by lmx on 2019/5/8.
 */
public abstract class AFilter implements IFilter
{
    private static final String TAG = "AFilter";


    protected Context mContext;

    protected int mSurfaceWidth;
    protected int mSurfaceHeight;

    protected GlMatrixTools mGlMatrixTools;

    //着色器语句
    protected final String mVertexShader;
    protected final String mFragmentShader;

    //着色器句柄
    protected int mVertexShaderHandle;
    protected int mFragmentShaderHandle;

    //执行句柄
    protected int mProgramHandle;
    //顶点坐标句柄
    protected int mPositionHandle;
    //纹理坐标句柄
    protected int mCoordinateHandle;
    //变换矩阵句柄
    protected int mMatrixHandle;
    //当前纹理句柄
    protected int mTextureHandle;

    private boolean mGLDraw = true;

    public AFilter(Context mContext, String mVertexShader, String mFragmentShader)
    {
        this.mContext = mContext;
        this.mVertexShader = mVertexShader;
        this.mFragmentShader = mFragmentShader;
    }

    public void onPause() {
        mGLDraw = false;
    }

    public void onResume() {
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config)
    {
        Log.d(TAG, "AFilter --> onSurfaceCreated: " + gl.toString());
        onSurfaceCreatedInit(config);
        mProgramHandle = onCreateProgram(config);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height)
    {
        Log.d(TAG, "AFilter --> onSurfaceChanged: " + gl.toString());
        setSurfaceSize(width, height);
        onSurfaceChangedInit(width, height);
    }

    private void setSurfaceSize(int width, int height)
    {
        mSurfaceWidth = width;
        mSurfaceHeight = height;
    }

    public int getProgram() {
        return mProgramHandle;
    }

    @Override
    public void onDrawFrame(GL10 gl)
    {
        Log.d(TAG, "AFilter --> onDrawFrame: ");
        onDraw();
    }

    public abstract void onSurfaceCreatedInit(EGLConfig eglConfig);
    public abstract void onSurfaceChangedInit(int width, int height);
    public abstract int onCreateProgram(EGLConfig eglConfig);
    public abstract void onDraw();

    protected void disuseProgram() {
        GLES20.glUseProgram(0);
    }

    @Override
    public void onRelease()
    {

    }
}
