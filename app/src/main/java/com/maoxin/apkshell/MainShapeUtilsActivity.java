package com.maoxin.apkshell;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.maoxin.apkshell.beauty.data.BeautyShapeDataUtils;
import com.maoxin.apkshell.beauty.data.ShapeDataType;

import java.util.ArrayList;

public class MainShapeUtilsActivity extends AppCompatActivity
{
    private static final String TAG = "MainShapeUtilsActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_shape_utils);

        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                runStart();
            }
        }).start();
    }

    private ArrayList<CoverBean> run(){
        ArrayList<CoverBean> list = new ArrayList<>();

        CoverBean coverBean = new CoverBean();
        coverBean.name = "瘦脸";
        float[] covertArea = new float[2];
        covertArea[0] = 0f;
        covertArea[1] = 90f;
        coverBean.area = covertArea;
        coverBean.shapeType = ShapeDataType.THINFACE;
        coverBean.ui = 5;
        list.add(coverBean);



        coverBean = new CoverBean();
        coverBean.name = "椭眼";
        covertArea = new float[2];
        covertArea[0] = 0f;
        covertArea[1] = 100f;
        coverBean.area = covertArea;
        coverBean.shapeType = ShapeDataType.BIGEYE;
        coverBean.ui = 7;
        list.add(coverBean);



        coverBean = new CoverBean();
        coverBean.name = "眼角";
        covertArea = new float[2];
        covertArea[0] = 20f;
        covertArea[1] = 80f;
        coverBean.area = covertArea;
        coverBean.shapeType = ShapeDataType.CANTHUS;
        coverBean.ui = 0;
        list.add(coverBean);



        coverBean = new CoverBean();
        coverBean.name = "颧骨";
        covertArea = new float[2];
        covertArea[0] = 0f;
        covertArea[1] = 100f;
        coverBean.area = covertArea;
        coverBean.shapeType = ShapeDataType.CHEEKBONES;
        coverBean.ui = 5;
        list.add(coverBean);



        coverBean = new CoverBean();
        coverBean.name = "下巴";
        covertArea = new float[2];
        covertArea[0] = 20f;
        covertArea[1] = 80f;
        coverBean.area = covertArea;
        coverBean.shapeType = ShapeDataType.CHIN;
        coverBean.ui = -2;
        list.add(coverBean);



        coverBean = new CoverBean();
        coverBean.name = "眼距";
        covertArea = new float[2];
        covertArea[0] = 20f;
        covertArea[1] = 80f;
        coverBean.area = covertArea;
        coverBean.shapeType = ShapeDataType.EYESPAN;
        coverBean.ui = -1;
        list.add(coverBean);



        coverBean = new CoverBean();
        coverBean.name = "额头";
        covertArea = new float[2];
        covertArea[0] = 0f;
        covertArea[1] = 100f;
        coverBean.area = covertArea;
        coverBean.shapeType = ShapeDataType.FOREHEAD;
        coverBean.ui = 1;
        list.add(coverBean);



        coverBean = new CoverBean();
        coverBean.name = "小脸";
        covertArea = new float[2];
        covertArea[0] = 0f;
        covertArea[1] = 80f;
        coverBean.area = covertArea;
        coverBean.shapeType = ShapeDataType.LITTLEFACE;
        coverBean.ui = 3;
        list.add(coverBean);



        coverBean = new CoverBean();
        coverBean.name = "嘴巴整体高度";
        covertArea = new float[2];
        covertArea[0] = 20f;
        covertArea[1] = 100f;
        coverBean.area = covertArea;
        coverBean.shapeType = ShapeDataType.MOUSEHEIGHT;
        coverBean.ui = 3.5f;
        list.add(coverBean);



        coverBean = new CoverBean();
        coverBean.name = "嘴巴";
        covertArea = new float[2];
        covertArea[0] = 20f;
        covertArea[1] = 100f;
        coverBean.area = covertArea;
        coverBean.shapeType = ShapeDataType.MOUTH;
        coverBean.ui = 2;
        list.add(coverBean);



        coverBean = new CoverBean();
        coverBean.name = "鼻高";
        covertArea = new float[2];
        covertArea[0] = 20f;
        covertArea[1] = 80f;
        coverBean.area = covertArea;
        coverBean.shapeType = ShapeDataType.NOSEHEIGHT;
        coverBean.ui = 2.7f;
        list.add(coverBean);



        coverBean = new CoverBean();
        coverBean.name = "鼻翼";
        covertArea = new float[2];
        covertArea[0] = 30f;
        covertArea[1] = 100f;
        coverBean.area = covertArea;
        coverBean.shapeType = ShapeDataType.NOSEWING;
        coverBean.ui = 2.5f;
        list.add(coverBean);



        coverBean = new CoverBean();
        coverBean.name = "削脸";
        covertArea = new float[2];
        covertArea[0] = 0f;
        covertArea[1] = 80f;
        coverBean.area = covertArea;
        coverBean.shapeType = ShapeDataType.SHAVEDFACE;
        coverBean.ui = 3;
        list.add(coverBean);



        coverBean = new CoverBean();
        coverBean.name = "瘦鼻";
        covertArea = new float[2];
        covertArea[0] = 0f;
        covertArea[1] = 80f;
        coverBean.area = covertArea;
        coverBean.shapeType = ShapeDataType.SHRINKNOSE;
        coverBean.ui = 2.5f;
        list.add(coverBean);



        coverBean = new CoverBean();
        coverBean.name = "微笑";
        covertArea = new float[2];
        covertArea[0] = 0f;
        covertArea[1] = 0f;
        coverBean.area = covertArea;
        coverBean.shapeType = ShapeDataType.SMILE;
        coverBean.ui = 0;
        list.add(coverBean);



        coverBean = new CoverBean();
        coverBean.name = "亮眼";
        covertArea = new float[2];
        covertArea[0] = 0f;
        covertArea[1] = 100f;
        coverBean.area = covertArea;
        coverBean.shapeType = ShapeDataType.EYEBRIGHT;
        coverBean.ui = 0;
        list.add(coverBean);



        coverBean = new CoverBean();
        coverBean.name = "祛眼袋";
        covertArea = new float[2];
        covertArea[0] = 0f;
        covertArea[1] = 100f;
        coverBean.area = covertArea;
        coverBean.shapeType = ShapeDataType.EYEBAGS;
        coverBean.ui = 0;
        list.add(coverBean);



        coverBean = new CoverBean();
        coverBean.name = "鼻尖";
        covertArea = new float[2];
        covertArea[0] = 20f;
        covertArea[1] = 100f;
        coverBean.area = covertArea;
        coverBean.shapeType = ShapeDataType.NOSETIP;
        coverBean.ui = 2;
        list.add(coverBean);



        coverBean = new CoverBean();
        coverBean.name = "鼻子立体";
        covertArea = new float[2];
        covertArea[0] = 0f;
        covertArea[1] = 100f;
        coverBean.area = covertArea;
        coverBean.shapeType = ShapeDataType.NOSEFACESHADOW;
        coverBean.ui = 0;
        list.add(coverBean);



        coverBean = new CoverBean();
        coverBean.name = "丰唇";
        covertArea = new float[2];
        covertArea[0] = 20f;
        covertArea[1] = 80f;
        coverBean.area = covertArea;
        coverBean.shapeType = ShapeDataType.MOUTHTHICKNESS;
        coverBean.ui = 0;
        list.add(coverBean);



        coverBean = new CoverBean();
        coverBean.name = "嘴宽";
        covertArea = new float[2];
        covertArea[0] = 20f;
        covertArea[1] = 80f;
        coverBean.area = covertArea;
        coverBean.shapeType = ShapeDataType.MOUTHWIDTH;
        coverBean.ui = -1.7f;
        list.add(coverBean);



        coverBean = new CoverBean();
        coverBean.name = "鼻梁";
        covertArea = new float[2];
        covertArea[0] = 30f;
        covertArea[1] = 100f;
        coverBean.area = covertArea;
        coverBean.shapeType = ShapeDataType.NOSERIDGE;
        coverBean.ui = 0;
        list.add(coverBean);



        coverBean = new CoverBean();
        coverBean.name = "清晰";
        covertArea = new float[2];
        covertArea[0] = 0f;
        covertArea[1] = 100f;
        coverBean.area = covertArea;
        coverBean.shapeType = ShapeDataType.CLARITYALPHA;
        coverBean.ui = 0;
        list.add(coverBean);



        coverBean = new CoverBean();
        coverBean.name = "肤色";
        covertArea = new float[2];
        covertArea[0] = 0f;
        covertArea[1] = 100f;
        coverBean.area = covertArea;
        coverBean.shapeType = ShapeDataType.SKINWHITENING;
        coverBean.ui = 0;
        list.add(coverBean);



        coverBean = new CoverBean();
        coverBean.name = "美肤";
        covertArea = new float[2];
        covertArea[0] = 0f;
        covertArea[1] = 100f;
        coverBean.area = covertArea;
        coverBean.shapeType = ShapeDataType.SMOOTHSKIN;
        coverBean.ui = 0;
        list.add(coverBean);



        coverBean = new CoverBean();
        coverBean.name = "美牙";
        covertArea = new float[2];
        covertArea[0] = 0f;
        covertArea[1] = 100f;
        coverBean.area = covertArea;
        coverBean.shapeType = ShapeDataType.TEETHWHITENING;
        coverBean.ui = 0;
        list.add(coverBean);


        return list;
    }

    /**
     * 瘦脸、小脸
     * @return
     */
    private ArrayList<CoverBean> run2()
    {
        ArrayList<CoverBean> list = new ArrayList<>();

        CoverBean coverBean = new CoverBean();
        coverBean.mode = "我的";
        coverBean.name = "小脸";
        coverBean.shapeType = ShapeDataType.LITTLEFACE;
        coverBean.ui = 2.1f;
        list.add(coverBean);

        coverBean = new CoverBean();
        coverBean.mode = "我的";
        coverBean.name = "瘦脸";
        coverBean.shapeType = ShapeDataType.THINFACE;
        coverBean.ui = 3.5f;
        list.add(coverBean);

        coverBean = new CoverBean();
        coverBean.mode = "仙女脸";
        coverBean.name = "小脸";
        coverBean.shapeType = ShapeDataType.LITTLEFACE;
        coverBean.ui = 3.8f;
        list.add(coverBean);

        coverBean = new CoverBean();
        coverBean.mode = "仙女脸";
        coverBean.name = "瘦脸";
        coverBean.shapeType = ShapeDataType.THINFACE;
        coverBean.ui = 4.3f;
        list.add(coverBean);

        coverBean = new CoverBean();
        coverBean.mode = "女神脸";
        coverBean.name = "小脸";
        coverBean.shapeType = ShapeDataType.LITTLEFACE;
        coverBean.ui = 2.6f;
        list.add(coverBean);

        coverBean = new CoverBean();
        coverBean.mode = "女神脸";
        coverBean.name = "瘦脸";
        coverBean.shapeType = ShapeDataType.THINFACE;
        coverBean.ui = 4f;
        list.add(coverBean);

        coverBean = new CoverBean();
        coverBean.mode = "甜心脸";
        coverBean.name = "小脸";
        coverBean.shapeType = ShapeDataType.LITTLEFACE;
        coverBean.ui = 5.4f;
        list.add(coverBean);

        coverBean = new CoverBean();
        coverBean.mode = "甜心脸";
        coverBean.name = "瘦脸";
        coverBean.shapeType = ShapeDataType.THINFACE;
        coverBean.ui = 3f;
        list.add(coverBean);


        coverBean = new CoverBean();
        coverBean.mode = "精致脸";
        coverBean.name = "小脸";
        coverBean.shapeType = ShapeDataType.LITTLEFACE;
        coverBean.ui = 0f;
        list.add(coverBean);

        coverBean = new CoverBean();
        coverBean.mode = "精致脸";
        coverBean.name = "瘦脸";
        coverBean.shapeType = ShapeDataType.THINFACE;
        coverBean.ui = 4.2f;
        list.add(coverBean);


        coverBean = new CoverBean();
        coverBean.mode = "果汁脸";
        coverBean.name = "小脸";
        coverBean.shapeType = ShapeDataType.LITTLEFACE;
        coverBean.ui = 4.6f;
        list.add(coverBean);

        coverBean = new CoverBean();
        coverBean.mode = "果汁脸";
        coverBean.name = "瘦脸";
        coverBean.shapeType = ShapeDataType.THINFACE;
        coverBean.ui = 2.9f;
        list.add(coverBean);

        coverBean = new CoverBean();
        coverBean.mode = "超模脸";
        coverBean.name = "小脸";
        coverBean.shapeType = ShapeDataType.LITTLEFACE;
        coverBean.ui = 0f;
        list.add(coverBean);

        coverBean = new CoverBean();
        coverBean.mode = "超模脸";
        coverBean.name = "瘦脸";
        coverBean.shapeType = ShapeDataType.THINFACE;
        coverBean.ui = 4.3f;
        list.add(coverBean);

        return list;
    }

    private void runStart() {
        // ArrayList<CoverBean> run = run();
        ArrayList<CoverBean> run = run2();
        for (CoverBean coverBean : run)
        {
            coverBean.nativenum = BeautyShapeDataUtils.GetReal4UIRate(coverBean.ui, coverBean.shapeType);

            System.out.println( " ---> " + coverBean.mode + ", " + coverBean.name + " , ui:" + coverBean.ui + ", native:" + coverBean.nativenum);
        }
    }

    public static final class CoverBean
    {
        String mode;
        String name;
        float[] area;
        float ui;
        float nativenum;
        @ShapeDataType
        int shapeType;
    }
}
