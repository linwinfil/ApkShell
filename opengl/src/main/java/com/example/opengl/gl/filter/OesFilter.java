package com.example.opengl.gl.filter;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.net.Uri;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.view.Surface;

import com.example.opengl.gl.utils.GlMatrixTools;
import com.example.opengl.gl.utils.GlUtils;

import javax.microedition.khronos.egl.EGLConfig;

/**
 * @author lmx
 * Created by lmx on 2019/5/21.
 */
public class OesFilter extends AFilter implements MediaPlayer.OnVideoSizeChangedListener,
        MediaPlayer.OnPreparedListener, SurfaceTexture.OnFrameAvailableListener
{
    private Uri mUri;
    private MediaPlayer mMediaPlayer;
    private SurfaceTexture mSurfaceTexture;
    private Surface mSurface;


    //oes的纹理矩阵
    private int mTexMatrixHandle;
    private int mInputTextureId = GlUtils.NO_TEXTURE;
    private float[] mSTMatrix = new float[16];
    private boolean isPrepared;

    private OnCallbackListenerAdapter mOnCallbackListenerAdapter;

    public OesFilter(Context mContext, String mVertexShader, String mFragmentShader)
    {
        super(mContext, mVertexShader, mFragmentShader);

        //上下纹理翻转
        mCoordinateBuffer.put(new float[]{
                0.0f,1.0f,
                0f, 0f,
                1.0f,1.0f,
                1.0f,0.0f
        }).position(0);
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
    public int getTextureType()
    {
        return GLES11Ext.GL_TEXTURE_EXTERNAL_OES;
    }

    @Override
    public int onCreateProgram(EGLConfig eglConfig)
    {
        int program = super.onCreateProgram(eglConfig);
        mTexMatrixHandle = GLES20.glGetUniformLocation(program, "vTexMatrix");
        return program;
    }

    @Override
    public void onDraw()
    {
        onClear();
        GLES20.glViewport(0, 0, mSurfaceWidth, mSurfaceHeight);
        if (!isPrepared) return;
        if (mInputTextureId == GlUtils.NO_TEXTURE || mSurfaceTexture == null) return;

        synchronized (this) {
            mSurfaceTexture.updateTexImage();
            mSurfaceTexture.getTransformMatrix(mSTMatrix);
        }

        //加载程序句柄
        GLES20.glUseProgram(mProgramHandle);

        //给视图矩阵赋值
        GLES20.glUniformMatrix4fv(mMatrixHandle, 1, false, mGlMatrixTools.getFinalMatrix(), 0);

        //oes纹理的矩阵
        GLES20.glUniformMatrix4fv(mTexMatrixHandle, 1, false, mSTMatrix, 0);

        //绑定纹理
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(getTextureType(), mInputTextureId);
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
        GLES20.glBindTexture(getTextureType(), 0);
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
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
            isPrepared = false;
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
            throw new IllegalStateException("prepare", e);
        }

        mMediaPlayer.setSurface(mSurface);
        mMediaPlayer.setLooping(true);
        mMediaPlayer.setOnVideoSizeChangedListener(this);
        mMediaPlayer.setOnPreparedListener(this);
        mMediaPlayer.prepareAsync();
    }

    @Override
    public void onPause()
    {
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
        }
    }

    @Override
    public void onResume() {
        if (mMediaPlayer != null) {
            mMediaPlayer.start();
        }
    }

    private void setVideoSize(int videoWidth, int videoHeight)
    {
        if (videoWidth > 0 && videoHeight > 0)
        {
            int width = mSurfaceWidth;
            int height = mSurfaceHeight;

            //eyez <= near < far
            float near = 3.0f;
            float far = 9.0f;
            float eyez = near;

            GlMatrixTools matrix = getMatrix();
            //设置相机位置
            matrix.setCamera(
                    0f, 0f, eyez,   //相机位置（eyez <= far）
                    0f, 0f, 0f,     //观察点
                    0f, 1f, 0f      //辅助向上量
            );

            float videoWH = videoWidth * 1f / videoHeight;
            float viewWH = width * 1f / height;
            if (videoWH > viewWH) {
                matrix.frustum(-1, 1, -videoWH / viewWH, videoWH / viewWH, near, far);
            } else {
                matrix.frustum(-viewWH / videoWH, viewWH / videoWH, -1, 1, near, far);
            }
        }
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
    public void onRelease()
    {
        super.onRelease();
        mOnCallbackListenerAdapter = null;
        disuseProgram();
        unbindTextureId();
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    @Override
    public void onVideoSizeChanged(MediaPlayer mp, int width, int height)
    {
        setVideoSize(width, height);
        if (mOnCallbackListenerAdapter != null) {
            mOnCallbackListenerAdapter.onVideoSizeChanged(mp, width, height);
        }
    }

    @Override
    public void onPrepared(MediaPlayer mp)
    {
        isPrepared = true;
        mp.start();
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
