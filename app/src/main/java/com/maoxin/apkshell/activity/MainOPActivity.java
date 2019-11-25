package com.maoxin.apkshell.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;

import com.maoxin.apkshell.R;

import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class MainOPActivity extends AppCompatActivity
{

    HashMap<String, Object> params = null;

    Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_op);

        mHandler = new Handler(new Handler.Callback()
        {
            @Override
            public boolean handleMessage(Message msg)
            {
                if (msg.what == 1)
                {
                    Button button = (Button) msg.obj;
                    button.setVisibility(View.VISIBLE);
                    return true;
                }
                else if (msg.what == 2)
                {

                }
                return false;
            }
        });

        findViewById(R.id.button6).setOnClickListener(v ->
        {
            // TEST();
        });

        findViewById(R.id.button7).setOnClickListener(v ->
        {
            Intent intent = new Intent(MainOPActivity.this, Main4Activity.class);
            startActivity(intent);
        });

        findViewById(R.id.button_mp4parser).setOnClickListener(v -> startActivity(new Intent(MainOPActivity.this, Main5Activity.class)));

        findViewById(R.id.button_mp4parser_ex).setOnClickListener(v -> startActivity(new Intent(MainOPActivity.this, Main5ExActivity.class)));

        final Button button_test_cmdproc = findViewById(R.id.button_test_cmdproc);
        button_test_cmdproc.setOnClickListener(v -> startActivity(new Intent(MainOPActivity.this, Main7Activity.class)));

        findViewById(R.id.button_test_animation).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                final Button button = (Button) v;
                ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(button, "alpha", 1, 0);
                objectAnimator.setStartDelay(1000);
                objectAnimator.setDuration(500);
                objectAnimator.addListener(new AnimatorListenerAdapter()
                {
                    @Override
                    public void onAnimationEnd(Animator animation)
                    {
                        super.onAnimationEnd(animation);
                        button.setVisibility(View.GONE);
                        Message message = mHandler.obtainMessage();
                        message.what = 1;
                        message.obj = button;
                        mHandler.sendMessageDelayed(message, 1000);
                    }
                });
                objectAnimator.start();
            }
        });

        findViewById(R.id.button_shape_utils).setOnClickListener(v -> startActivity(new Intent(MainOPActivity.this, MainShapeUtilsActivity.class)));

        findViewById(R.id.concurrent_test).setOnClickListener(v -> startActivity(new Intent(MainOPActivity.this, Main8Activity.class)));

        findViewById(R.id.action_test).setOnClickListener(v -> startActivity(new Intent(MainOPActivity.this, Main9Activity.class)));

        findViewById(R.id.test_new_shape_args).setOnClickListener(v -> startActivity(new Intent(MainOPActivity.this, Main10Activity.class)));

        findViewById(R.id.test_kotlin).setOnClickListener(v -> startActivity(new Intent(MainOPActivity.this, MainKotlinActivity.class)));

        findViewById(R.id.test_task_stack_builder).setOnClickListener(v -> startActivity(new Intent(MainOPActivity.this, Main12Activity.class)));

        findViewById(R.id.test_open_camerax).setOnClickListener(v -> startActivity(new Intent(MainOPActivity.this, MainCameraXActivity.class)));

        findViewById(R.id.test_dispatch_touch).setOnClickListener(v -> {
            startActivity(new Intent(MainOPActivity.this, Main2Activity.class));
        });

        findViewById(R.id.test_android10_storage).setOnClickListener(v -> {
            startActivity(new Intent(MainOPActivity.this, Main13Activity.class));
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
