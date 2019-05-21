package com.example.opengl.gl.filter;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.net.Uri;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.view.Surface;

import com.example.opengl.gl.utils.GlUtils;

import javax.microedition.khronos.egl.EGLConfig;

/**
 * @author lmx
 * Created by lmx on 2019/5/21.
 */
public class VideoFilter extends AFilter implements MediaPlayer.OnVideoSizeChangedListener, MediaPlayer.OnPreparedListener, SurfaceTexture.OnFrameAvailableListener
{
    Uri mUri;
    MediaPlayer mMediaPlayer;
    SurfaceTexture mSurfaceTexture;
    Surface mSurface;

    int mInputTextureId = GlUtils.NO_TEXTURE;

    OnCallbackListenerAdapter mOnCallbackListenerAdapter;

    float[] mSTMatrix = new float[16];


    public VideoFilter(Context mContext, String mVertexShader, String mFragmentShader)
    {
        super(mContext, mVertexShader, mFragmentShader);
    }

    @Override
    public void onSurfaceCreatedInit(EGLConfig eglConfig)
    {
        unbindTextureId();
        mInputTextureId = GlUtils.createTextureOES();
        if (mSurfaceTexture != null)
        {
            mSurfaceTexture.release();
        }
        if (mSurface != null)
        {
            mSurface.release();
        }
        mSurfaceTexture = new SurfaceTexture(mInputTextureId);
        mSurfaceTexture.setOnFrameAvailableListener(this);

        mSurface = new Surface(mSurfaceTexture);
    }

    @Override
    public void onSurfaceChangedInit(int width, int height)
    {
        GLES20.glViewport(0, 0, width, height);
    }

    @Override
    public int onCreateProgram(EGLConfig eglConfig)
    {
        mVertexShaderHandle = GlUtils.loadShader(mVertexShader, GLES20.GL_VERTEX_SHADER);
        mFragmentShaderHandle = GlUtils.loadShader(mFragmentShader, GLES20.GL_FRAGMENT_SHADER);
        mProgramHandle = GlUtils.loadProgram(mVertexShaderHandle, mFragmentShaderHandle);

        mPositionHandle = GLES20.glGetAttribLocation(mProgramHandle, "vPosition");
        mCoordinateHandle = GLES20.glGetAttribLocation(mProgramHandle, "vCoordinate");
        mMatrixHandle = GLES20.glGetUniformLocation(mProgramHandle, "vMatrix");
        mTextureHandle = GLES20.glGetUniformLocation(mProgramHandle, "vTexture");//oes纹理

        return mProgramHandle;
    }

    @Override
    public void onDraw()
    {
        onClear();
        GLES20.glViewport(0, 0, mSurfaceWidth, mSurfaceHeight);
        if (mInputTextureId == GlUtils.NO_TEXTURE || mSurfaceTexture == null) return;


        synchronized (this) {
            mSurfaceTexture.updateTexImage();
            mSurfaceTexture.getTransformMatrix(mSTMatrix);
        }

        //加载程序句柄
        GLES20.glUseProgram(mProgramHandle);

        //给视图矩阵赋值
        /*GLES20.glUniformMatrix4fv(mMatrixHandle, 1, false, , 0);*/


        //给纹理单元分配一个默认值
        GLES20.glUniform1i(mTextureHandle, 0);

        //启用顶点坐标句柄
        GLES20.glEnableVertexAttribArray(mPositionHandle);
        //传入顶点坐标数据
        GLES20.glVertexAttribPointer(mPositionHandle, 2, GLES20.GL_FLOAT, false, 0, mPositionBuffer);

        //启用纹理坐标句柄
        GLES20.glEnableVertexAttribArray(mCoordinateHandle);
        //传如纹理坐标数据
        GLES20.glVertexAttribPointer(mCoordinateHandle, 2, GLES20.GL_FLOAT, false, 0 , mCoordinateBuffer);

        //绘制模式
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);

        //解绑坐标，解绑 纹理
        GLES20.glDisableVertexAttribArray(mPositionHandle);
        GLES20.glDisableVertexAttribArray(mCoordinateHandle);
    }

    public void setOnCallbackListenerAdapter(OnCallbackListenerAdapter mOnCallbackListenerAdapter)
    {
        this.mOnCallbackListenerAdapter = mOnCallbackListenerAdapter;
    }

    public void setData(Uri uri)
    {
        this.mUri = uri;
    }

    public void prepare()
    {
        if (mMediaPlayer != null)
        {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }

        mMediaPlayer = new MediaPlayer();
        try
        {
            if (mUri != null)
            {
                mMediaPlayer.setDataSource(mContext, mUri);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        mMediaPlayer.setLooping(true);
        mMediaPlayer.setOnVideoSizeChangedListener(this);
        mMediaPlayer.setOnPreparedListener(this);

        mMediaPlayer.prepareAsync();
    }

    protected void unbindTextureId()
    {
        if (mInputTextureId != GlUtils.NO_TEXTURE)
        {
            GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, 0);
            GLES20.glDeleteTextures(1, new int[]{mInputTextureId}, 0);
            mInputTextureId = GlUtils.NO_TEXTURE;
        }
    }

    @Override
    public void onVideoSizeChanged(MediaPlayer mp, int width, int height)
    {

    }

    @Override
    public void onPrepared(MediaPlayer mp)
    {
        if (mOnCallbackListenerAdapter != null) {
            mOnCallbackListenerAdapter.onPrepared(mp);
        }
    }

    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture)
    {
        if (mOnCallbackListenerAdapter != null) {
            mOnCallbackListenerAdapter.onFrameAvailable(surfaceTexture);
        }
    }

    public static abstract class OnCallbackListenerAdapter
            implements SurfaceTexture.OnFrameAvailableListener, MediaPlayer.OnPreparedListener,
            MediaPlayer.OnVideoSizeChangedListener, MediaPlayer.OnCompletionListener
    {
        @Override
        public void onFrameAvailable(SurfaceTexture surfaceTexture)
        {

        }

        @Override
        public void onPrepared(MediaPlayer mp)
        {

        }

        @Override
        public void onVideoSizeChanged(MediaPlayer mp, int width, int height)
        {

        }

        @Override
        public void onCompletion(MediaPlayer mp)
        {

        }
    }
}
