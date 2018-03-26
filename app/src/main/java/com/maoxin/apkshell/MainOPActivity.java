package com.maoxin.apkshell;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import java.util.HashMap;

public class MainOPActivity extends AppCompatActivity
{

    HashMap<String, Object> params = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_op);

        findViewById(R.id.button6).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                test();
            }
        });

        findViewById(R.id.button7).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(MainOPActivity.this, Main4Activity.class);
                startActivity(intent);
            }
        });
    }

    private void test()
    {
        if (params == null)
        {
            params = new HashMap<>();
            params.put("1", true);
            params.put("2", 1);
            params.put("3", (short) 1);
            params.put("4", 1L);
            params.put("5", 1.0F);
            params.put("6", 1.1D);
            params.put("7", "aa");
            params.put("8", null);
            params.put("9", (byte) 0);
            params.put("10", new Object());
        }

        boolean b = getValue(params, "1", false, Boolean.class);
        int i = getValue(params, "2", 2, Integer.class);
        short s = getValue(params, "3", (short) 2, Short.class);
        long l = getValue(params, "4", 2L, Long.class);
        float f = getValue(params, "5", 1.1F, Float.class);
        double d = getValue(params, "6", 2.0D, Double.class);
        String str = getValue(params, "7", "bb", String.class);
        Void aNull = getValue(params, "8", null, Void.class);
        byte bt = getValue(params, "9", (byte) 1, Byte.class);
        Object obj = getValue(params, "10", "object", Object.class);
        String nullStr = getValue(params, "11", null, String.class);

        System.out.print("");
    }

    public <T> T getValue(HashMap<String, Object> params, @NonNull String key, T defValue, Class<T> clazz)
    {
        if (params == null) return defValue;
        Object o = params.get(key);
        if (o == null) return defValue;
        if (clazz.isAssignableFrom(o.getClass()))
        {
            return (T) o;
        }
        return defValue;
    }
}
