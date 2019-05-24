package com.example.opengl.gl.filter;

import android.content.Context;
import android.opengl.GLES20;

import com.example.opengl.gl.utils.GlMatrixTools;

import javax.microedition.khronos.egl.EGLConfig;

import androidx.annotation.FloatRange;

/**
 * @author lmx
 * Created by lmx on 2019/5/24.
 */
public class ShapeFilter extends AFilter
{

    @FloatRange(from = 0.0f, to = 1.0f)
    private float mRatio = 0.5f;
    @FloatRange(from = 0.0f, to = 1.0f)
    private float mAlpha = 1.0f;

    //调节范围句柄
    private int mRatioHandle;
    //透明度句柄
    private int mAlphaHandle;

    public ShapeFilter(Context mContext, String mVertexShader, String mFragmentShader)
    {
        super(mContext, mVertexShader, mFragmentShader);
    }

    public void setRatio(float mRatio)
    {
        this.mRatio = mRatio;
    }

    public void setAlpha(float mAlpha)
    {
        this.mAlpha = mAlpha;
    }

    @Override
    public void onSurfaceCreatedInit(EGLConfig eglConfig)
    {
        unbindTextureId();
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

        if (width >= height)
        {
            matrix.frustum(-1, 1, -height * 1f / width, height * 1f / width, near, far);
        }
        else
        {
            matrix.frustum(-width * 1f / height, width * 1f / height, -1, 1, near, far);
        }
    }

    @Override
    public int onCreateProgram(EGLConfig eglConfig)
    {
       int program = super.onCreateProgram(eglConfig);
       mRatioHandle = GLES20.glGetUniformLocation(program, "aRatio");
       mAlphaHandle = GLES20.glGetUniformLocation(program, "aAlhpa");
       return program;
    }

    @Override
    public void onDraw()
    {
        //清屏颜色与深度
        onClear();

        //加载程序句柄
        GLES20.glUseProgram(mProgramHandle);

        GLES20.glViewport(0, 0, mSurfaceWidth, mSurfaceHeight);

        //启用顶点坐标句柄
        GLES20.glEnableVertexAttribArray(mPositionHandle);
        //传入顶点坐标数据
        GLES20.glVertexAttribPointer(mPositionHandle, 2, GLES20.GL_FLOAT, false, 0, mPositionBuffer);

        //启用纹理坐标句柄
        GLES20.glEnableVertexAttribArray(mCoordinateHandle);
        //传如纹理坐标数据
        GLES20.glVertexAttribPointer(mCoordinateHandle, 2, GLES20.GL_FLOAT, false, 0 , mCoordinateBuffer);

        //给视图矩阵赋值
        GLES20.glUniformMatrix4fv(mMatrixHandle, 1, false, mGlMatrixTools.getFinalMatrix(), 0);

        GLES20.glUniform1f(mAlphaHandle, mAlpha);
        GLES20.glUniform1f(mRatioHandle, mRatio);


        //绘制模式
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);

        GLES20.glViewport(0, 0, mSurfaceWidth, mSurfaceHeight);

        //解绑
        GLES20.glDisableVertexAttribArray(mPositionHandle);
        GLES20.glDisableVertexAttribArray(mCoordinateHandle);
        GLES20.glBindTexture(getTextureType(), 0);
        disuseProgram();
    }

    @Override
    public void onClear()
    {
        super.onClear();
        GLES20.glClearColor(0f, 0f, 0f, 1f);
    }

    @Override
    public int getTextureType()
    {
        return GLES20.GL_TEXTURE_2D;
    }
}
