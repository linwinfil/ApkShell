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
import android.widget.EditText;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.maoxin.apkshell.R;
import com.maoxin.apkshell.ipc.client.ClientActivity;
import com.maoxin.apkshell.kotlin.example.recyclerview.MainKotlinRecyclerViewActivity;
import com.maoxin.appinstaller.entity.AppUpdate;
import com.maoxin.appinstaller.utils.UpdateManager;

import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

@Route(path = "/activity/main_op")
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

        findViewById(R.id.test_kotlin).setOnClickListener(v -> startActivity(new Intent(MainOPActivity.this, MainKotlinRecyclerViewActivity.class)));

        findViewById(R.id.test_task_stack_builder).setOnClickListener(v -> startActivity(new Intent(MainOPActivity.this, Main12Activity.class)));

        findViewById(R.id.test_open_camerax).setOnClickListener(v -> startActivity(new Intent(MainOPActivity.this, MainCameraXActivity.class)));

        findViewById(R.id.test_dispatch_touch).setOnClickListener(v -> {
            startActivity(new Intent(MainOPActivity.this, Main2Activity.class));
        });

        findViewById(R.id.test_android10_storage).setOnClickListener(v -> {
            // startActivity(new Intent(MainOPActivity.this, Main13Activity.class));
            startActivity(new Intent(MainOPActivity.this, Main13KotlinActivity.class));
        });

        findViewById(R.id.test_binder_ipc).setOnClickListener(v -> {
            startActivity(new Intent(MainOPActivity.this, ClientActivity.class));
        });

        findViewById(R.id.test_arouter).setOnClickListener(v -> {
            ARouter.getInstance().build("/activity/main_13")
                    .withBoolean("isARouter", true)
                    .withBoolean("isLogin", false)
                    .withString("fromClass", MainOPActivity.this.getClass().getSimpleName())
                    .navigation();
        });

        findViewById(R.id.test_appinstaller).setOnClickListener(v -> {
            // 更新的数据参数
            AppUpdate appUpdate = new AppUpdate.Builder()
                    //更新地址（必传）
                    .newVersionUrl("http://c.adnonstop.com/camera21_201911152102.apk")
                    // 版本号（非必填）
                    .newVersionCode("v2.4.5")
                    // 更新的标题，弹框的标题（非必填，默认为应用更新）
                    .updateTitle(R.string.update_title)
                    // 更新内容的提示语，内容的标题（非必填，默认为更新内容）
                    .updateContentTitle(R.string.update_content_lb)
                    // 更新内容（非必填，默认“1.用户体验优化\n2.部分问题修复”）
                    .updateInfo("1.用户体验优化\n2.部分问题修复")
                    // 文件大小（非必填）
                    .fileSize("5.8M")
                    .savePath("")
                    //是否采取静默下载模式（非必填，只显示更新提示，后台下载完自动弹出安装界面），否则，显示下载进度，显示下载失败
                    .isSilentMode(true)
                    //是否强制更新（非必填，默认不采取强制更新，否则，不更新无法使用）
                    .forceUpdate(0)
                    //文件的MD5值，默认不传，如果不传，不会去验证md5(非静默下载模式生效，若有值，且验证不一致，会启动浏览器去下载)
                    .md5("")
                    .build();
            new UpdateManager().startUpdate(MainOPActivity.this, appUpdate);
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
