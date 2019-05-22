package com.example.opengl.gl.filter;

import android.content.Context;
import android.opengl.GLES20;
import android.text.TextUtils;
import android.util.Log;

import com.example.opengl.gl.utils.GlMatrixTools;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

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

    private OnFilterListener mOnFilterListener;

    protected FloatBuffer mPositionBuffer;
    protected FloatBuffer mCoordinateBuffer;

    //顶点坐标
    protected final float[] positionPoint = {
            -1.0f, 1.0f, //左上角
            -1.0f, -1.0f,//左下角
            1.0f, 1.0f,//右上角
            1.0f, -1.0f//右下角
    };

    //纹理坐标（对应顶点坐标）
    protected final float[] coordinatePoint = {
            0f, 0f,
            0.0f,1.0f,
            1.0f,0.0f,
            1.0f,1.0f,
    };

    public AFilter(Context mContext, String mVertexShader, String mFragmentShader)
    {
        this.mContext = mContext;
        this.mVertexShader = mVertexShader;
        this.mFragmentShader = mFragmentShader;
        this.mGlMatrixTools = new GlMatrixTools();

        if (TextUtils.isEmpty(mVertexShader) || TextUtils.isEmpty(mFragmentShader)) {
            throw new IllegalStateException("vertex or fragment is null");
        }

        FloatBuffer floatBuffer = ByteBuffer.allocateDirect(positionPoint.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer().put(positionPoint);
        floatBuffer.position(0);
        mPositionBuffer = floatBuffer;

        floatBuffer = ByteBuffer.allocateDirect(coordinatePoint.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer().put(coordinatePoint);
        floatBuffer.position(0);
        mCoordinateBuffer = floatBuffer;
    }

    public GlMatrixTools getMatrix()
    {
        return mGlMatrixTools;
    }

    public void onPause() {
        mGLDraw = false;
    }

    public void onResume() {
    }

    public void setOnFilterListener(OnFilterListener mOnFilterListener)
    {
        this.mOnFilterListener = mOnFilterListener;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config)
    {
        Log.d(TAG, "AFilter --> onSurfaceCreated: " + gl.toString());
        mProgramHandle = onCreateProgram(config);
        onSurfaceCreatedInit(config);
        if (mOnFilterListener != null) {
            mOnFilterListener.onSurfaceCreated();
        }
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height)
    {
        Log.d(TAG, "AFilter --> onSurfaceChanged: " + gl.toString());
        setSurfaceSize(width, height);
        onSurfaceChangedInit(width, height);
        if (mOnFilterListener != null) {
            mOnFilterListener.onSurfaceChanged(width, height);
        }
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
    public abstract int getTextureType();


    protected void checkProgram(int program) {
        if (program == 0) {
            throw new IllegalStateException("create gl program error");
        }
    }

    protected void disuseProgram() {
        GLES20.glUseProgram(0);
    }

    protected void onUserProgram() {
        GLES20.glUseProgram(mProgramHandle);
    }

    public void onClear() {
        GLES20.glClearColor(1f, 1f, 1f, 1f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
    }

    @Override
    public void onRelease()
    {

    }
}
