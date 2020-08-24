package com.maoxin.apkshell.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;

import com.maoxin.apkshell.R;


/**
 * Created by Yif on 2018/4/23.
 */

public class AutoZoomQRIconView extends BeautyViewEx {

    private Shape frame;
    private Paint paint;

    public AutoZoomQRIconView(Context context) {
        super(context);
    }

    @Override
    protected void InitData() {
        super.InitData();
        paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
        frame = new Shape();
        frame.m_bmp = BitmapFactory.decodeResource(getResources(), R.drawable.qr_cut_img_border_square);
    }

    @Override
    public void setImage(Bitmap bitmap) {
        super.setImage(bitmap);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //绘制蒙层
        if (frame != null && frame.m_bmp != null && !frame.m_bmp.isRecycled()) {
            canvas.save();
            canvas.translate(mCanvasX, mCanvasY);
            canvas.concat(global.m_matrix);
            canvas.drawBitmap(frame.m_bmp, frame.m_matrix, null);
            canvas.restore();
        }
    }
}