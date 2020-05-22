package com.maoxin.apkshell.activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.maoxin.apkshell.R;

import java.util.Arrays;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * @author lmx
 * Created by lmx on 2020/5/22.
 */
public class MainCanvasActivity extends AppCompatActivity {
    private FrameLayout inflate;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inflate = (FrameLayout) getLayoutInflater().inflate(R.layout.activity_main_canvas, null, false);
        setContentView(inflate);

        inflate.findViewById(R.id.btn_click).setOnClickListener(v -> {
            inflate.invalidate();
        });

        DrawView drawView = new DrawView(this);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        layoutParams.topMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, getResources().getDisplayMetrics());
        inflate.addView(drawView, layoutParams);
    }

    @Override
    protected void onResume() {
        super.onResume();
        new Handler().postDelayed(() -> inflate.invalidate(), 600);
    }

    static class Shape {
        Matrix m_matrix = new Matrix();
        Bitmap m_bmp;
        Object m_ex;
        Object m_info;

        Matrix CloneMatrix()
        {
            return new Matrix(m_matrix);
        }
    }


    private float downX;
    private float downY;
    private float moveX;
    private float moveY;
    private float upX;
    private float upY;

    private boolean isTouch;

    Shape target;
    Shape shape;
    Paint paint;

    public class DrawView extends View {
        DrawView(Context context) {
            super(context);
            paint = new Paint();
            shape = new Shape();
            shape.m_bmp = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
            shape.m_matrix.reset();
            shape.m_matrix.postTranslate(200, 400);
            shape.m_matrix.postScale(1.5f, 1.5f);
        }


        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            canvas.save();
            canvas.drawColor(Color.BLACK);

            paint.reset();
            paint.setAntiAlias(true);
            paint.setFilterBitmap(true);
            canvas.drawBitmap(shape.m_bmp, shape.m_matrix, paint);
            canvas.restore();

        }

        @Override
        public boolean onTouchEvent(MotionEvent event)
        {
            switch (event.getActionMasked() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN: {
                    isTouch = true;
                    downX = event.getX();
                    downY = event.getY();
                    return oddDown(event);
                }
                case MotionEvent.ACTION_MOVE: {
                    if (event.getPointerCount() > 1) {
                        evenMove(event);
                    } else {
                        moveX = event.getX();
                        moveY = event.getY();
                        oddMove(event);
                    }
                    break;
                }
                case MotionEvent.ACTION_UP: {
                    isTouch = false;
                    upX = event.getX();
                    upY = event.getY();
                    oddUp(event);
                    break;
                }

                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_OUTSIDE: {
                    isTouch = false;
                    break;
                }
            }
            return true;
        }

    }


    private boolean oddDown(MotionEvent event) {
        float[] pts = new float[]{downX, downY};

        final float[] dst = new float[pts.length];
        final float[] src = new float[pts.length];
        System.arraycopy(pts, 0, src, 0, pts.length);
        inverseCount(dst, src, shape.m_matrix);
        boolean isHit = false;
        if (0 <= dst[0] && dst[0] <= shape.m_bmp.getWidth()) {
            if (0 <= dst[1] && dst[1] <= shape.m_bmp.getHeight()) {
                isHit = true;
            }
        }
        target = isHit ? shape : null;

        Log.i("@@@", ("oddDown --> dst:" + Arrays.toString(dst) + "isHit:" + isHit));
        return isHit;
    }

    private void oddUp(MotionEvent event) {
        if (target != null) {
            float[] imgShowPos = getImgShowPos(target);
            if (imgShowPos != null) {
                Log.i("@@@", "oddUp --> showPos:" + Arrays.toString(imgShowPos));
            }
        }
    }

    private void oddMove(MotionEvent event) {

    }

    private void evenMove(MotionEvent event) {

    }


    private void inverseCount(float[] dst, float[] src, Matrix matrix)
    {
        if (matrix == null || src == null || src.length <= 0) {
            return;
        }

        Canvas canvas = new Canvas();
        canvas.concat(matrix);

        Matrix temp = new Matrix();
        canvas.getMatrix(temp);
        Matrix invert = new Matrix();
        temp.invert(invert);
        invert.mapPoints(dst, src);
    }

    /**
     * 基于屏幕左上角为原点，换算图片位置 --->> 屏幕坐标
     *
     * @return 返回长度8的数组 0->lt_x 1->lt_y 顺时针如此类推
     */
    protected float[] getImgShowPos(Shape target)
    {
        if (target.m_bmp == null) {
            return null;
        }

        float[] src = new float[8];
        float[] dst = new float[8];

        // left-top
        src[0] = 0;
        src[1] = 0;
        // right-top
        src[2] = target.m_bmp.getWidth();
        src[3] = 0;
        // right-bottom
        src[4] = target.m_bmp.getWidth();
        src[5] = target.m_bmp.getHeight();
        // left-bottom
        src[6] = 0;
        src[7] = target.m_bmp.getHeight();

        // 自身变换
        target.m_matrix.mapPoints(dst, src);
        return dst;
    }

}
