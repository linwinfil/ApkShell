package com.maoxin.apkshell;

import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;

import com.maoxin.apkshell.beauty.data.SuperShapeDataTestRunner;
import com.maoxin.apkshell.beauty.data.base.ShapeArgs;

import java.util.ArrayList;

public class Main10Activity extends AppCompatActivity
{
    private static final String TAG = "Main10Activity";


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main10);

        new Handler(Looper.getMainLooper()).postDelayed(() -> new Thread(Main10Activity.this::runTest2).start(), 300);
    }

    private void runTest()
    {
        SparseArray<ShapeArgs> shapeArgs = SuperShapeDataTestRunner.getShapeArgs();

        ArrayList<SuperShapeDataTestRunner.ShapeValue> value_4_ui_default = SuperShapeDataTestRunner.get_value_4_ui_default();
        for (SuperShapeDataTestRunner.ShapeValue value : value_4_ui_default)
        {
            value.setArgs(SuperShapeDataTestRunner.getShapeArgs(shapeArgs, value.getType()));
            Log.i(TAG, "runTest: " + value.getValue_LogString());
        }
        Log.d(TAG, "Main10Activity --> runTest: end~~~~~~~~~~~~");
    }


    private void runTest2()
    {
        ArrayList<SuperShapeDataTestRunner.SpecialValue> natural = SuperShapeDataTestRunner.get_special_value_4_ui_natural();
        ArrayList<SuperShapeDataTestRunner.SpecialValue> circle = SuperShapeDataTestRunner.get_special_value_4_ui_circle();
        ArrayList<SuperShapeDataTestRunner.SpecialValue> slim = SuperShapeDataTestRunner.get_special_value_4_ui_slim();

        for (SuperShapeDataTestRunner.SpecialValue value : natural)
        {
            Log.i(TAG, "runTest2: natural:" + value.getValue_LogString());
        }
        Log.i(TAG, "runTest2: natural end~~~~~~~~");

        for (SuperShapeDataTestRunner.SpecialValue value : circle)
        {
            Log.i(TAG, "runTest2: circle:" + value.getValue_LogString());
        }
        Log.i(TAG, "runTest2: circle end~~~~~~~~");

        for (SuperShapeDataTestRunner.SpecialValue value : slim)
        {
            Log.i(TAG, "runTest2: slim:" + value.getValue_LogString());
        }
        Log.i(TAG, "runTest2: slim end~~~~~~~~");
    }
}
