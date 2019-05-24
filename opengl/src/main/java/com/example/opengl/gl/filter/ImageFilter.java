package com.example.opengl.gl.filter;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLES20;

import com.example.opengl.gl.utils.GlMatrixTools;
import com.example.opengl.gl.utils.GlUtils;

import javax.microedition.khronos.egl.EGLConfig;

/**
 * @author lmx
 * Created by lmx on 2019/5/9.
 */
public class ImageFilter extends AFilter
{
    Bitmap mBitmap;
    private boolean mRefresh;
    private boolean mDrawMulti;

    private boolean mRequestAnimation;

    private volatile float mAnimationFactor;
    private float mAngle;

    private volatile float mSweptAngle;
    private volatile float mTempAngle;
    private volatile float mTempScale;
    private volatile float mTempTransX;
    private volatile float mTempTransY;

    private int mTempFip;


    public ImageFilter(Context mContext, String mVertexShader, String mFragmentShader)
    {
        super(mContext, mVertexShader, mFragmentShader);

        mAngle = 0f;
        mAnimationFactor = 0f;

        mTempScale = 1f;
        mTempAngle = 0f;
        mTempTransX = 0f;
        mTempTransY = 0f;
        mSweptAngle = 0f;

        mTempFip = 0;
    }

    public void setBitmap(Bitmap bitmap) {
        this.mBitmap = bitmap;
        this.mRefresh = true;
        unbindTextureId();
    }

    public void setDrawMulti(boolean drawMulti) {
        this.mDrawMulti = drawMulti;
    }
    public boolean isDrawMulti() {
        return mDrawMulti;
    }

    public void setRequestAnimation(boolean requestAnimation) {
        this.mRequestAnimation = requestAnimation;
    }

    public void setAnimationFactor(float animationFactor) {
        this.mAnimationFactor = animationFactor;
    }

    public void setSweptAngle(int angle) {
        mSweptAngle = angle;
    }

    public void requestUpdateAngle() {
        mAngle = (mAngle + mSweptAngle) % 360;
        mSweptAngle = 0;
    }

    public void setFlipHorizontal(boolean flipHorizontal) {
        if (flipHorizontal && ((mTempFip & Flip_Horizontal) == 0)) {
            mTempFip |= Flip_Horizontal;
        } else if (!flipHorizontal && ((mTempFip & Flip_Horizontal) != 0)){
           mTempFip &= ~Flip_Horizontal;
        }
    }

    public void setFlipVertical(boolean flipVertical) {
        if (flipVertical && ((mTempFip & Flip_Vertical) == 0)) {
            mTempFip |= Flip_Vertical;
        } else if (!flipVertical && ((mTempFip & Flip_Vertical) != 0)) {
            mTempFip &= ~Flip_Vertical;
        }
    }

    private boolean isFlipHorizontal() {
        return (mTempFip & Flip_Horizontal) != 0;
    }

    private boolean isFlipVertical() {
        return (mTempFip & Flip_Vertical) != 0;
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
    }

    @Override
    public int getTextureType()
    {
        return GLES20.GL_TEXTURE_2D;
    }

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

            // float bWH = bw * 1f / bh;
            // float vWH = width * 1f / height;
            // if (bWH > vWH) {
            //     matrix.frustum(-1, 1, - bWH / vWH, bWH / vWH, near, far);
            // } else {
            //     matrix.frustum(-vWH / bWH, vWH / bWH, - 1, 0, near, far);
            // }

            //保护缩放旋转操作前矩阵
            matrix.pushMatrix();

            // 逻辑：如何理解旋转要考虑缩放问题？要清楚旋转之前，纹理的宽高缩放比例是基于什么角度的！！！
            // 假设，原纹理属性 (基于0°时) textureW = 1, textureH = 0.5，而且原纹理需要被填充到(w = 3, h = 4)的区域中，非铺满填充 mScaleFullIn = false，
            // 现在要将原纹理 顺时针旋转 90°，那么旋转后纹理的属性应该发生变化 textureW = 0.5， textureH = 1，
            // 那么此时，应该1、先旋转后缩放，还是2、先缩放后旋转呢，其实选择1和2都可以，但是要根据不同的纹理属性来考虑：
            //
            // 基于1、先旋转后缩放：正确代码应该是 基于旋转后的纹理属性(textureW = 0.5， textureH = 1)来做缩放
            // matrix.scale();
            // matrix.rotate();
            //
            // 基于2、先缩放后旋转：正确代码应该是 基于旋转前的纹理属性(textureW = 1, textureH = 0.5)来做缩放
            // matrix.rotate();
            // matrix.scale();

            float scale = 1f;
            float transX = 0;
            float transY = 0;
            float angle = 0;

            //计算缩放，旋转，位移
            int textureW = bw;
            int textureH = bh;
            float x_scale = textureW >= textureH ? 1f : textureW * 1f/ textureH;
            float y_scale = textureH > textureW ? 1f : textureH * 1f / textureW;
            if (mRequestAnimation) {
                float sweptAngle = mSweptAngle;
                float nextAngle = (mAngle + sweptAngle) % 360;
                mTempScale = handleAnimationScale(width, height, textureW, textureH, mAngle, nextAngle, mAnimationFactor);
                mTempTransX = 0;
                mTempTransY = 0;
                mTempAngle = mAngle + sweptAngle * mAnimationFactor;
            } else {
                mTempScale = handleStaticScale(width, height, textureW, textureH, mAngle);
                mTempTransX = 0;
                mTempTransY = 0;
                mTempAngle = mAngle;
            }

            scale = mTempScale;
            transX = mTempTransX;
            transY = mTempTransY;
            angle = mTempAngle;

            // GL 矩阵是前乘关系，即右乘矩阵（粗暴理解，后写的代码先执行），旋转要考虑缩放问题
            matrix.translate(transX, transY, 0);
            matrix.rotate(-angle, 0, 0, 1);
            //基于近平面顶点坐标乘上缩放的系数
            float scaleX = scale;
            float scaleY = scale;
            if (isFlipVertical()) {
                scaleY *= -1;
            }
            if (isFlipHorizontal()) {
                scaleX *= -1;
            }
            matrix.scale(x_scale * scaleX, y_scale * scaleY, 1f);
        }
    }

    private float handleAnimationScale(int viewportW, int viewportH, int textureW, int textureH, float currentDegree, float nextDegree, float animFactor)
    {
        currentDegree = Math.abs(currentDegree);
        nextDegree = Math.abs(nextDegree);

        int textureWidth = textureW;
        int textureHeight = textureH;

        if ((currentDegree >= 90 && currentDegree < 180) || currentDegree >= 270) {
            textureWidth = textureWidth + textureHeight;
            textureHeight = textureWidth - textureHeight;
            textureWidth = textureWidth - textureHeight;
        }

        float[] floats = handleFrameScale(viewportW, viewportH, textureWidth, textureHeight);
        float frameSizeUS = floats[0];
        float frameSizeVS = floats[1];

        int tempW = viewportW;
        int tempH = viewportH;

        float tempUS = tempW >= tempH ? 1f : (float) tempW / tempH;
        float tempVS = tempH > tempW ? 1f : (float) tempH / tempW;

        float currentDegreeScale;
        if (frameSizeUS >= tempUS) {
            currentDegreeScale = Math.min(tempUS / frameSizeUS, tempVS / frameSizeVS);
        } else {
            currentDegreeScale = Math.max(frameSizeUS / tempUS, frameSizeVS / tempVS);
        }

        if (currentDegree != nextDegree) {

            textureWidth = textureW;
            textureHeight = textureH;

            if (nextDegree == 90 || nextDegree == 270) {
                textureWidth = textureWidth + textureHeight;
                textureHeight = textureWidth - textureHeight;
                textureWidth = textureWidth - textureHeight;
            }

            floats = handleFrameScale(viewportW, viewportH, textureWidth, textureHeight);
            frameSizeUS = floats[0];
            frameSizeVS = floats[1];

            tempW = viewportW;
            tempH = viewportH;
            tempUS = tempW >= tempH ? 1f : (float) tempW / tempH;
            tempVS = tempH > tempW ? 1f : (float) tempH / tempW;

            float nextDegreeScale;
            if (frameSizeUS >= tempUS) {
                nextDegreeScale = Math.min(tempUS / frameSizeUS, tempVS / frameSizeVS);
            } else {
                nextDegreeScale = Math.max(frameSizeUS / tempUS, frameSizeVS / tempVS);
            }

            return currentDegreeScale + (nextDegreeScale - currentDegreeScale) * animFactor;
        }

        return currentDegreeScale;

    }

    protected float handleStaticScale(int viewportW, int viewportH, int textureW, int textureH, float currentDegree)
    {
        currentDegree = Math.abs(currentDegree);

        int textureWidth = textureW;
        int textureHeight = textureH;

        if (currentDegree % 180 == 90)
        {
            textureWidth = textureWidth + textureHeight;
            textureHeight = textureWidth - textureHeight;
            textureWidth = textureWidth - textureHeight;
        }

        float[] floats = handleFrameScale(viewportW, viewportH, textureWidth, textureHeight);
        float frameSizeUS = floats[0];
        float frameSizeVS = floats[1];

        float viewportUS = viewportW >= viewportH ? 1f : viewportW * 1f / viewportH;
        float viewportVS = viewportH > viewportW ? 1f : viewportH * 1f / viewportW;

        if (frameSizeUS >= viewportUS) {
            return Math.min(viewportUS / frameSizeUS, viewportVS / frameSizeVS);
        } else {
            return Math.max(frameSizeUS / viewportUS, frameSizeVS / viewportVS);
        }
    }


    private float[] handleFrameScale(int viewportW, int viewportH, int textureW, int textureH)
    {
        //顶点坐标x轴位置
        float frameSizeUS;
        //顶点坐标y轴位置
        float frameSizeVS;

        if (textureW >= textureH)
        {
            frameSizeUS = 1.0f;
            frameSizeVS = textureH * 1f / textureW;
        }
        else
        {
            frameSizeUS = textureW * 1f / textureH;
            frameSizeVS = 1.0f;
        }

        return new float[]{frameSizeUS, frameSizeVS};
    }

    @Override
    public void onDraw()
    {
        //清屏颜色与深度
        onClear();

        if (mBitmap == null || mBitmap.isRecycled()) return;
        setMatrix();
        if (mTextureId == GlUtils.NO_TEXTURE) {
            mTextureId = GlUtils.createTexture(mBitmap);
        }

        //加载程序句柄
        GLES20.glUseProgram(mProgramHandle);

        int size = 1;
        if (mDrawMulti) {
            size = 2;
        }

        for (int i = 0; i < size; i++)
        {
            if (size == 1) {
                GLES20.glViewport(0, 0, mSurfaceWidth, mSurfaceHeight);
            } else {
                if (i == 0) {
                    GLES20.glViewport(0, 0, mSurfaceWidth / 2, mSurfaceHeight / 2);
                } else if (i == 1) {
                    GLES20.glViewport(mSurfaceWidth / 2, mSurfaceHeight / 2, mSurfaceWidth / 2, mSurfaceHeight / 2);
                }
            }
            //给视图矩阵赋值
            GLES20.glUniformMatrix4fv(mMatrixHandle, 1, false, mGlMatrixTools.getFinalMatrix(), 0);

            //绑定纹理
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
            GLES20.glBindTexture(getTextureType(), mTextureId);

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
        }

        GLES20.glViewport(0, 0, mSurfaceWidth, mSurfaceHeight);

        //还原矩阵
        mGlMatrixTools.popMatrix();

        //解绑
        GLES20.glDisableVertexAttribArray(mPositionHandle);
        GLES20.glDisableVertexAttribArray(mCoordinateHandle);
        GLES20.glBindTexture(getTextureType(), 0);
        disuseProgram();
    }

    @Override
    public int onCreateProgram(EGLConfig eglConfig)
    {
        return super.onCreateProgram(eglConfig);
    }

    @Override
    public void onRelease()
    {
        super.onRelease();
        unbindTextureId();
        disuseProgram();
    }

}