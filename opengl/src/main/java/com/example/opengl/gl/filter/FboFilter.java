package com.example.opengl.gl.filter;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLES30;
import android.opengl.GLUtils;

import com.example.opengl.R;
import com.example.opengl.gl.utils.Drawable2d;
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

    //处理阈值 句柄
    protected int mProgressHandle;


    protected int[] mFrameBuffers = new int[1];
    protected int[] mTextures = new int[2];

    private onCaptureCallback mOnCaptureCallback;


    private float mProgress = 50f;
    private Bitmap mBitmap;
    private Drawable2d drawable2d;

    private boolean isCapture = false;

    public FboFilter(Context mContext)
    {
        super(mContext, GlUtils.loadShaderRawResource(mContext, R.raw.default_vertex_shader),
                GlUtils.loadShaderRawResource(mContext, R.raw.color_fragment_shader));
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
    protected void initTexCoordinateBuffer() {
        drawable2d = new Drawable2d();
    }

    @Override
    public void onSurfaceCreatedInit(EGLConfig eglConfig)
    {
        unbindFrameBuffer();
    }

    @Override
    public int getTextureType()
    {
        return GLES30.GL_TEXTURE_2D;
    }

    @Override
    public void onSurfaceChangedInit(int width, int height)
    {
        GLES30.glViewport(0, 0, width, height);
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
        int program = super.onCreateProgram(eglConfig);
        mProgressHandle = GLES30.glGetUniformLocation(program, "vProgress");
        return program;
    }

    @Override
    public void onClear()
    {
        GLES30.glClearColor(0.5f, 0.5f, 0.5f, 1f);
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT | GLES30.GL_DEPTH_BUFFER_BIT);
    }

    @Override
    public void onDraw()
    {
        //清屏
        onClear();

        if (mBitmap == null || mBitmap.isRecycled()) return;

        int bw = mBitmap.getWidth();
        int bh = mBitmap.getHeight();
        int viewportw = mSurfaceWidth;
        int viewporth = mSurfaceHeight;
        GlMatrixTools matrix = getMatrix();
        //保存相机视口的矩阵
        matrix.pushMatrix();

        float scale = handleStaticScale(mSurfaceWidth, mSurfaceHeight, bw, bh);
        float x_scale = bw >= bh ? 1f : bw * 1f / bh;
        float y_scale = bh > bw ? 1f : bh * 1f / bw;

        //这里做了垂直翻转
        matrix.scale(x_scale * scale, -1 * y_scale * scale, 1f);


        //创建buffer和纹理
        createFrameBuffer();
        //绑定buffer，绘制buffer的帧缓冲区域
        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, mFrameBuffers[0]);
        //创建的 frame buffer 挂载一个texture，用来储存颜色
        GLES30.glFramebufferTexture2D(GLES30.GL_FRAMEBUFFER, GLES30.GL_COLOR_ATTACHMENT0, getTextureType(), mTextures[1], 0);
        //buffer绘制视图窗口
        GLES30.glViewport(0, 0, viewportw, viewporth);

        //启用程序
        GLES30.glUseProgram(mProgramHandle);

        // 1 ======

        //将当前bitmap的纹理绘制到帧缓冲区域（frameBuffer）上
        GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
        GLES30.glBindTexture(getTextureType(), mTextures[0]);
        GLES30.glUniform1i(mTextureHandle, 0);

        //参数赋值
        GLES30.glUniform1f(mProgressHandle, mProgress);

        //给视图矩阵赋值
        GLES30.glUniformMatrix4fv(mMatrixHandle, 1, false, mGlMatrixTools.getFinalMatrix(), 0);
        //顶点和片元
        GLES30.glEnableVertexAttribArray(mPositionHandle);
        GLES30.glVertexAttribPointer(mPositionHandle, drawable2d.getCoordsPerVertex(), GLES30.GL_FLOAT, false, drawable2d.getVertexStride(), drawable2d.getVertexArray());
        GLES30.glEnableVertexAttribArray(mCoordinateHandle);
        GLES30.glVertexAttribPointer(mCoordinateHandle, drawable2d.getCoordsPerVertex(), GLES30.GL_FLOAT, false, drawable2d.getTexCoordStride(), drawable2d.getTexCoordArray());

        //绘制模式
        GLES30.glDrawArrays(GLES30.GL_TRIANGLE_STRIP, 0, 4);

        //解绑坐标
        GLES30.glDisableVertexAttribArray(mPositionHandle);
        GLES30.glDisableVertexAttribArray(mCoordinateHandle);

        //解绑frameBuffer
        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, 0);

        matrix.popMatrix();

        // 2 ======

        //还原视图窗口
        GLES30.glViewport(0, 0, viewportw, viewporth);

        //将帧缓冲区域的纹理绘制到当前屏幕上
        GLES30.glActiveTexture(GLES30.GL_TEXTURE1);
        GLES30.glBindTexture(getTextureType(), mTextures[1]);
        GLES30.glUniform1i(mTextureHandle, 1);

        GLES30.glUniformMatrix4fv(mMatrixHandle, 1, false, getMatrix().getOpenGLUnitMatrix(), 0);//这里为默认矩阵单元

        //顶点和片元
        GLES30.glEnableVertexAttribArray(mPositionHandle);
        GLES30.glVertexAttribPointer(mPositionHandle, drawable2d.getCoordsPerVertex(), GLES30.GL_FLOAT, false, drawable2d.getVertexStride(), drawable2d.getVertexArray());
        GLES30.glEnableVertexAttribArray(mCoordinateHandle);
        GLES30.glVertexAttribPointer(mCoordinateHandle, drawable2d.getCoordsPerVertex(), GLES30.GL_FLOAT, false, drawable2d.getTexCoordStride(), drawable2d.getTexCoordArray());

        GLES30.glDrawArrays(GLES30.GL_TRIANGLE_STRIP, 0, 4);

        if (isCapture)
        {
            isCapture = false;
            int outw = mBitmap.getWidth();
            int outh = mBitmap.getHeight();
            ByteBuffer buffer = ByteBuffer.allocate(outw * outh * 4);
            buffer.rewind();
            GLES30.glReadPixels(0, 0, outw, outh, GLES30.GL_RGBA, GLES30.GL_UNSIGNED_BYTE, buffer);
            buffer.rewind();

            Bitmap bitmap = Bitmap.createBitmap(outw, outh, Bitmap.Config.ARGB_8888);
            bitmap.copyPixelsFromBuffer(buffer);
            buffer.clear();

            if (mOnCaptureCallback != null) mOnCaptureCallback.onCapture(bitmap);
        }

        GLES30.glDisableVertexAttribArray(mPositionHandle);
        GLES30.glDisableVertexAttribArray(mCoordinateHandle);

        unbindFrameBuffer();
        disuseProgram();
    }

    protected void createFrameBuffer()
    {
        //创建buffer
        GLES30.glGenFramebuffers(mFrameBuffers.length, mFrameBuffers, 0);

        //创建texture
        GLES30.glGenTextures(2, mTextures, 0);
        for (int i = 0; i < mTextures.length; i++)
        {
            GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, mTextures[i]);

            if (i == 0)
            {
                //图像纹理
                GLUtils.texImage2D(GLES30.GL_TEXTURE_2D, 0, GLES30.GL_RGBA, mBitmap, 0);
            }
            else
            {
                //frame纹理
                GLES30.glTexImage2D(GLES30.GL_TEXTURE_2D, 0, GLES30.GL_RGBA, mSurfaceWidth, mSurfaceHeight, 0, GLES30.GL_RGBA, GLES30.GL_UNSIGNED_BYTE, null);
            }

            //设置过滤属性
            GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_NEAREST);
            GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR);
            GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_S, GLES30.GL_CLAMP_TO_EDGE);
            GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_T, GLES30.GL_CLAMP_TO_EDGE);
        }
    }

    protected void unbindFrameBuffer()
    {
        GLES30.glDeleteTextures(mTextures.length, mTextures, 0);
        GLES30.glDeleteBuffers(mFrameBuffers.length, mFrameBuffers, 0);
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
