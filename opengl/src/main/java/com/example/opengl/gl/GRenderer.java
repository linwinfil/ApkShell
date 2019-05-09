package com.example.opengl.gl;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import com.example.opengl.gl.filter.AFilter;
import com.example.opengl.gl.utils.GlUtils;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * @author lmx
 * Created by lmx on 2019/5/8.
 */
public class GRenderer implements GLSurfaceView.Renderer
{

    public abstract class OnSurfaceListener
    {
        public void onSurfaceCreated() {}

        public void onSurfaceChanged(int width, int height) {}

        public void onDrawFrame() {}
    }

    private AFilter mFilter;
    private Context mContext;
    private OnSurfaceListener mListener;
    private int mWidth, mHeight;
    private Bitmap mBitmap;


    private float[] mProjectMatrix = new float[16];
    private float[] mMvpMatrix = new float[16];

    private int mTextureId = GlUtils.NO_TEXTURE;

    public GRenderer(Context mContext)
    {
        this.mContext = mContext;
    }

    public void setBitmap(Bitmap bitmap) {
        this.mBitmap = bitmap;
    }

    public void setOnSurfaceListener(OnSurfaceListener listener)
    {
        this.mListener = listener;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config)
    {
        GLES20.glClearColor(0f, 0f, 0f, 1f);

        if (mFilter != null) {
            mFilter.onSurfaceCreated(config);
        }
        if (mListener != null) {
            mListener.onSurfaceCreated();
        }
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height)
    {
        mWidth = width;
        mHeight = height;
        GLES20.glViewport(0, 0, width, height);

        if (mBitmap != null && !mBitmap.isRecycled())
        {
            int bw = mBitmap.getWidth();
            int bh = mBitmap.getHeight();
            float bWH = bw * 1f / bh;
            float vWH = mWidth * 1f / mHeight;

            //near < far
            float near = 3.0f;
            float far = 9.0f;
            if (bWH > vWH) {
                Matrix.setIdentityM(mProjectMatrix, 0);
                Matrix.frustumM(mProjectMatrix, 0, -1, 1, - bWH / vWH, bWH / vWH, near, far);
            } else {
                Matrix.setIdentityM(mProjectMatrix, 0);
                Matrix.frustumM(mProjectMatrix, 0, -vWH / bWH, vWH / bWH, - 1, 0, near, far);
            }
            float eyez = near;
            //设置相机位置
            float[] cameraMatrix = new float[16];
            Matrix.setLookAtM(cameraMatrix,
                    0,              //偏移量，一般为0
                    0, 0, eyez,     //相机位置（eyez <= far）
                    0f, 0f, 0f,     //观察点
                    0f, 1.0f, 0.0f  //辅助向上量
            );
            //计算变化矩阵
            Matrix.setIdentityM(mMvpMatrix, 0);
            Matrix.multiplyMM(mMvpMatrix, 0, mProjectMatrix, 0, cameraMatrix, 0);
        }

        if (mFilter != null) {
            mFilter.onSurfaceChanged(width, height);
        }
        if (mListener != null)
        {
            mListener.onSurfaceChanged(width, height);
        }
    }

    @Override
    public void onDrawFrame(GL10 gl)
    {
        if (mFilter != null) {
            if (mTextureId == GlUtils.NO_TEXTURE) {
                mTextureId = GlUtils.createTexture(mBitmap);
            }
            mFilter.onDrawFrame(mTextureId, mMvpMatrix, null);
        }
        if (mListener != null)
        {
            mListener.onDrawFrame();
        }
    }

    public void onRelease()
    {
        mListener = null;
    }
}
