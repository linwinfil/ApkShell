package com.example.opengl.gl.filter;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLUtils;

import com.example.opengl.gl.utils.GlMatrixTools;
import com.example.opengl.gl.utils.GlUtils;

import java.nio.ByteBuffer;

import javax.microedition.khronos.egl.EGLConfig;

/**
 * @author lmx
 * Created by lmx on 2019/5/14.
 */
public class FboFilter extends AFilter
{

    public interface onCaptureCallback
    {
        void onCapture(Bitmap bitmap);
    }

    protected short[] vertexPointIndex = new short[]{
            0, 1, 2,
            0, 2, 3
    };

    //处理阈值 句柄
    protected int mProgressHandle;


    protected int[] mFrameBuffers = new int[1];
    protected int[] mTextures = new int[2];

    private onCaptureCallback mOnCaptureCallback;


    private float mProgress = 50f;
    private Bitmap mBitmap;

    private boolean isCapture = false;

    public FboFilter(Context mContext, String mVertexShader, String mFragmentShader)
    {
        super(mContext, mVertexShader, mFragmentShader);
    }

    public void setOnCaptureCallback(onCaptureCallback mOnCaptureCallback)
    {
        this.mOnCaptureCallback = mOnCaptureCallback;
    }

    public void setProgress(int progress)
    {
        if (progress > 100)
        {
            progress = 100;
        }
        else if (progress < 0)
        {
            progress = 0;
        }
        mProgress = progress;
    }

    public void setBitmap(Bitmap bitmap)
    {
        this.mBitmap = bitmap;
    }

    public void setCapture(boolean capture)
    {
        isCapture = capture;
    }

    @Override
    public void onSurfaceCreatedInit(EGLConfig eglConfig)
    {
        unbindFrameBuffer();
    }

    @Override
    public void onSurfaceChangedInit(int width, int height)
    {
        GLES20.glViewport(0, 0, width, height);
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
        if (width >= height) {
            matrix.frustum(-1, 1, -height * 1f / width, height * 1f / width, near, far);
        } else {
            matrix.frustum(-width * 1f / height, width * 1f / height, -1, 1, near, far);
        }
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
        mTextureHandle = GLES20.glGetUniformLocation(mProgramHandle, "vTexture");

        mProgressHandle = GLES20.glGetUniformLocation(mProgramHandle, "vProgress");

        return mProgramHandle;
    }

    @Override
    public void onClear()
    {
        GLES20.glClearColor(0.5f, 0.5f, 0.5f, 1f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
    }

    @Override
    public void onDraw()
    {
        //清屏
        onClear();

        if (mBitmap == null || mBitmap.isRecycled()) return;

        int bw = mBitmap.getWidth();
        int bh = mBitmap.getHeight();

        //创建buffer
        createFrameBuffer();

        //绑定buffer，绘制buffer的帧缓冲区域
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, mFrameBuffers[0]);
        //创建的 frame buffer 挂载一个texture，储存颜色
        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0, GLES20.GL_TEXTURE_2D, mTextures[1], 0);
        //buffer绘制视图窗口
        int viewportw = mSurfaceWidth;
        int viewporth = mSurfaceHeight;
        GLES20.glViewport(0, 0, viewportw, viewporth);

        GlMatrixTools matrix = getMatrix();
        //保存相机视口的矩阵
        matrix.pushMatrix();

        float scale = handleStaticScale(mSurfaceWidth, mSurfaceHeight, bw, bh);
        float x_scale = bw >= bh ? 1f : bw * 1f / bh;
        float y_scale = bh > bw ? 1f : bh * 1f / bw;

        //这里做了垂直翻转
        matrix.scale(x_scale * scale, -1 * y_scale * scale, 1f);

        //启用程序
        GLES20.glUseProgram(mProgramHandle);

        // 1 ======

        //绘制纹理，当前绘制到帧缓冲区域上
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextures[0]);
        GLES20.glUniform1i(mTextureHandle, 0);

        //参数赋值
        GLES20.glUniform1f(mProgressHandle, mProgress);

        //给视图矩阵赋值
        GLES20.glUniformMatrix4fv(mMatrixHandle, 1, false, mGlMatrixTools.getFinalMatrix(), 0);

        GLES20.glEnableVertexAttribArray(mPositionHandle);
        GLES20.glVertexAttribPointer(mPositionHandle, 2, GLES20.GL_FLOAT, false, 0, mPositionBuffer);

        GLES20.glEnableVertexAttribArray(mCoordinateHandle);
        GLES20.glVertexAttribPointer(mCoordinateHandle, 2, GLES20.GL_FLOAT, false, 0 , mCoordinateBuffer);

        //绘制模式
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);

        //解绑坐标
        GLES20.glDisableVertexAttribArray(mPositionHandle);
        GLES20.glDisableVertexAttribArray(mCoordinateHandle);

        matrix.popMatrix();

        // 2 ======


        //设置fbo为默认
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
        //还原视图窗口
        GLES20.glViewport(0, 0, viewportw, viewporth);

        //将帧缓冲区域的纹理绘制到当前屏幕上
        GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextures[1]);
        GLES20.glUniform1i(mTextureHandle, 1);

        GLES20.glUniformMatrix4fv(mMatrixHandle, 1, false, getMatrix().getOpenGLUnitMatrix(), 0);

        GLES20.glEnableVertexAttribArray(mPositionHandle);
        GLES20.glVertexAttribPointer(mPositionHandle, 2, GLES20.GL_FLOAT, false, 0, mPositionBuffer);
        GLES20.glEnableVertexAttribArray(mCoordinateHandle);
        GLES20.glVertexAttribPointer(mCoordinateHandle, 2, GLES20.GL_FLOAT, false, 0, mCoordinateBuffer);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);

        if (isCapture)
        {
            isCapture = false;
            int outw = mBitmap.getWidth();
            int outh = mBitmap.getHeight();
            ByteBuffer buffer = ByteBuffer.allocate(outw * outh * 4);
            buffer.rewind();
            GLES20.glReadPixels(0, 0, outw, outh, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, buffer);
            buffer.rewind();

            Bitmap bitmap = Bitmap.createBitmap(outw, outh, Bitmap.Config.ARGB_8888);
            bitmap.copyPixelsFromBuffer(buffer);
            buffer.clear();

            if (mOnCaptureCallback != null) mOnCaptureCallback.onCapture(bitmap);
        }

        GLES20.glDisableVertexAttribArray(mPositionHandle);
        GLES20.glDisableVertexAttribArray(mCoordinateHandle);

        unbindFrameBuffer();
        disuseProgram();
    }

    protected void createFrameBuffer()
    {
        //创建buffer
        GLES20.glGenFramebuffers(mFrameBuffers.length, mFrameBuffers, 0);

        //创建texture
        GLES20.glGenTextures(2, mTextures, 0);
        for (int i = 0; i < mTextures.length; i++)
        {
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextures[i]);

            if (i == 0)
            {
                //图像纹理
                GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, mBitmap, 0);
            }
            else
            {
                int width = mSurfaceWidth;
                int height = mSurfaceHeight;
                //frame纹理
                GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, width, height, 0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null);
            }

            //设置过滤属性
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
        }
    }

    protected void unbindFrameBuffer()
    {
        GLES20.glDeleteTextures(mTextures.length, mTextures, 0);
        GLES20.glDeleteBuffers(mFrameBuffers.length, mFrameBuffers, 0);
    }

    private float handleStaticScale(int viewportW, int viewportH, int textureW, int textureH)
    {
        //顶点坐标x轴位置
        float frameSizeUS = textureW >= textureH ? 1f : textureW * 1f / textureH;
        //顶点坐标y轴位置
        float frameSizeVS = textureH > textureW ? 1f : textureH * 1f / textureW;

        float viewportUS = viewportW >= viewportH ? 1f : viewportW * 1f / viewportH;
        float viewportVS = viewportH > viewportW ? 1f : viewportH * 1f / viewportW;

        if (frameSizeUS >= viewportUS) {
            return Math.min(viewportUS / frameSizeUS, viewportVS / frameSizeVS);
        } else {
            return Math.max(frameSizeUS / viewportUS, frameSizeVS / viewportVS);
        }
    }

    @Override
    public void onRelease()
    {
        super.onRelease();
        unbindFrameBuffer();

    }
}
