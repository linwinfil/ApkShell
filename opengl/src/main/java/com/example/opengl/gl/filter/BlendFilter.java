package com.example.opengl.gl.filter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;

import com.example.opengl.R;
import com.example.opengl.gl.utils.GlMatrixTools;
import com.example.opengl.gl.utils.GlUtils;

import javax.microedition.khronos.egl.EGLConfig;

/**
 * @author lmx
 * Created by lmx on 2019/5/15.
 */
public class BlendFilter extends ImageFilter
{
    protected int mTextureWId = GlUtils.NO_TEXTURE;

    protected Bitmap mWaterBmp;

    protected int mBlendSrcInt = GLES20.GL_ONE;
    protected int mBlendDstInt = GLES20.GL_SRC_ALPHA;
    protected int mBlendFuncInt = GLES20.GL_FUNC_ADD;

    public BlendFilter(Context mContext, String mVertexShader, String mFragmentShader)
    {
        super(mContext, mVertexShader, mFragmentShader);

        mWaterBmp = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.ic_launcher_round2);
    }

    @Override
    public void onSurfaceCreatedInit(EGLConfig eglConfig)
    {
        super.onSurfaceCreatedInit(eglConfig);

    }

    @Override
    public void onSurfaceChangedInit(int width, int height)
    {
        super.onSurfaceChangedInit(width, height);
    }


    public void setBlendSrcInt(int mBlendSrcInt)
    {
        this.mBlendSrcInt = mBlendSrcInt;
    }

    public void setBlendDstInt(int mBlendDstInt)
    {
        this.mBlendDstInt = mBlendDstInt;
    }

    public void setBlendFuncInt(int mBlendFuncInt)
    {
        this.mBlendFuncInt = mBlendFuncInt;
    }

    @Override
    public int onCreateProgram(EGLConfig eglConfig)
    {
        return super.onCreateProgram(eglConfig);
    }

    @Override
    public void onDraw()
    {
        onClear();
        if (mBitmap == null || mBitmap.isRecycled()) return;
        setMatrix();
        if (mTextureId == GlUtils.NO_TEXTURE) {
            mTextureId = GlUtils.createTexture(mBitmap);
        }
        if (mTextureWId == GlUtils.NO_TEXTURE) {
            mTextureWId = GlUtils.createTexture(mWaterBmp);
        }

        GLES20.glViewport(0, 0, mSurfaceWidth, mSurfaceHeight);

        //加载程序句柄
        GLES20.glUseProgram(mProgramHandle);

        {
            //启用混合
            GLES20.glDisable(GLES20.GL_DEPTH_TEST);
            GLES20.glEnable(GLES20.GL_BLEND);
            //混合操作
            GLES20.glBlendEquation(mBlendFuncInt);
            //混合方程
            // GLES20.glBlendFuncSeparate(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA, GLES20.GL_ONE, GLES20.GL_ONE);

            //设置源因子，目标因子
            GLES20.glBlendFunc(mBlendSrcInt, mBlendDstInt);
        }

        {
            //给视图矩阵赋值
            GLES20.glUniformMatrix4fv(mMatrixHandle, 1, false, mGlMatrixTools.getFinalMatrix(), 0);

            //绑定纹理
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureId);

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
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        }

        {
            //绑定水印纹理
            GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureWId);
            GLES20.glUniform1i(mTextureHandle, 1);

            GLES20.glEnableVertexAttribArray(mPositionHandle);
            GLES20.glVertexAttribPointer(mPositionHandle, 2, GLES20.GL_FLOAT, false, 0, mPositionBuffer);

            GLES20.glEnableVertexAttribArray(mCoordinateHandle);
            GLES20.glVertexAttribPointer(mCoordinateHandle, 2, GLES20.GL_FLOAT, false, 0 , mCoordinateBuffer);

            GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);

            //解绑坐标，解绑 纹理
            GLES20.glDisableVertexAttribArray(mPositionHandle);
            GLES20.glDisableVertexAttribArray(mCoordinateHandle);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        }


        //还原矩阵
        mGlMatrixTools.popMatrix();

        //关闭混合
        GLES20.glDisable(GLES20.GL_BLEND);
        disuseProgram();
    }

    @Override
    protected void setMatrix()
    {
        if (mBitmap != null && !mBitmap.isRecycled())
        {
            int width = mSurfaceWidth;
            int height = mSurfaceHeight;

            int bw = mBitmap.getWidth();
            int bh = mBitmap.getHeight();
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
            //保护缩放旋转操作前矩阵
            matrix.pushMatrix();

            float x_scale = bw >= bh ? 1f : bw * 1f/ bh;
            float y_scale = bh > bw ? 1f : bh * 1f / bw;
            float scale = handleStaticScale(width, height, bw, bh, 0);
            matrix.scale(x_scale * scale, y_scale * scale, 1f);
        }
    }

    @Override
    protected void unbindTextureId()
    {
        super.unbindTextureId();
        if (mTextureWId != GlUtils.NO_TEXTURE) {
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
            GLES20.glDeleteTextures(1, new int[]{mTextureWId}, 0);
            mTextureWId = GlUtils.NO_TEXTURE;
        }
    }
}
