package com.adnonstop.exoplayer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

/**
 * Created by Gxx on 2018/2/2.
 */

public class BufferSeekBar extends View
{
    private OnSeekBarChangeListener mListener;

    public interface OnSeekBarChangeListener
    {
        void onStartTrackingTouch(float percent);

        void onProgressChanged(float percent);

        void onStopTrackingTouch(float percent);
    }

    private int mRadius;
    private int mPointColor;
    private Paint mPaint;
    private int mPaintFlag;

    private int mBgColor;
    private int mProgressColor;
    private int mBufferColor;

    private int mProgressWidth;

    private int mViewW;
    private int mViewH;

    private float mProgress;
    private float mBufferProgress;

    private boolean mAlreadyBuffered;
    private boolean mUIEnable = true;
    private boolean mDown;

    public BufferSeekBar(Context context)
    {
        this(context, null);
    }

    public BufferSeekBar(Context context, @Nullable AttributeSet attrs)
    {
        this(context, attrs, 0);
    }

    public BufferSeekBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        mPaint = new Paint();
        mPaintFlag = Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG;
    }

    public void setOnSeekBarChangeListener(OnSeekBarChangeListener listener)
    {
        mListener = listener;
    }

    public void setPointParams(int radius, int color)
    {
        mRadius = radius;
        mPointColor = color;
    }

    public void setColor(int bg_color, int progress_color, int buffer_color)
    {
        mBgColor = bg_color;
        mProgressColor = progress_color;
        mBufferColor = buffer_color;
    }

    public void setProgressWidth(int width)
    {
        mProgressWidth = width;
    }

    public void setProgress(float percent)
    {
        if (mDown) return;

        mProgress = percent;
        update();
    }

    public void setBufferProgress(float percent)
    {
        if (mAlreadyBuffered)
        {
            return;
        }

        mBufferProgress = percent;
        if (mBufferProgress >= 1)
        {
            mAlreadyBuffered = true;
        }
        update();
    }

    public void update()
    {
        invalidate();
    }

    public void setUIEnable(boolean enable)
    {
        mUIEnable = enable;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh)
    {
        super.onSizeChanged(w, h, oldw, oldh);
        mViewW = w;
        mViewH = h;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        if (mUIEnable)
        {
            if (event.getPointerCount() > 1)
            {
                mDown = false;
                return false;
            }

            switch (event.getAction())
            {
                case MotionEvent.ACTION_DOWN:
                {
                    mDown = true;
                    mProgress = calculateProgress(event);
                    if (mListener != null)
                    {
                        mListener.onStartTrackingTouch(mProgress);
                    }
                    break;
                }

                case MotionEvent.ACTION_MOVE:
                {
                    if (mDown)
                    {
                        mProgress = calculateProgress(event);
                        if (mListener != null)
                        {
                            mListener.onProgressChanged(mProgress);
                        }
                    }
                    break;
                }

                case MotionEvent.ACTION_UP:
                {
                    if (mDown)
                    {
                        mProgress = calculateProgress(event);
                        if (mListener != null)
                        {
                            mListener.onStopTrackingTouch(mProgress);
                        }
                    }

                    mDown = false;
                    break;
                }
            }
            update();
            return true;
        }
        return super.onTouchEvent(event);
    }

    private float calculateProgress(MotionEvent event)
    {
        float x = event.getX();

        if (x <= mRadius)
        {
            x = mRadius;
        }
        else if (x >= mViewW - mRadius)
        {
            x = mViewW - mRadius;
        }

        float progress_len = mViewW - mRadius * 2f;

        return (x - mRadius) * 1f / progress_len;
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        drawProgress(canvas);
        drawProgressPoint(canvas);
    }

    private void drawProgressPoint(Canvas canvas)
    {
        canvas.save();

        float progress_len = mViewW - mRadius * 2f;

        float cen_x = mRadius + progress_len * mProgress;
        float cen_y = mViewH / 2f;

        mPaint.reset();
        mPaint.setFlags(mPaintFlag);
        mPaint.setColor(mPointColor);
        canvas.drawCircle(cen_x, cen_y, mRadius, mPaint);

        canvas.restore();
    }

    private void drawProgress(Canvas canvas)
    {
        canvas.save();

        mPaint.reset();
        mPaint.setFlags(mPaintFlag);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(mProgressWidth);
        mPaint.setColor(mBgColor);

        float progress_len = mViewW - mRadius * 2f;

        float start_x = mRadius;
        float end_x = mViewW - mRadius;
        float y = mViewH / 2f;

        canvas.drawLine(start_x, y, end_x, y, mPaint);

        mPaint.setColor(mBufferColor);
        end_x = start_x + progress_len * mBufferProgress;

        canvas.drawLine(start_x, y, end_x, y, mPaint);

        mPaint.setColor(mProgressColor);
        end_x = start_x + progress_len * mProgress;
        canvas.drawLine(start_x, y, end_x, y, mPaint);

        canvas.restore();
    }
}
