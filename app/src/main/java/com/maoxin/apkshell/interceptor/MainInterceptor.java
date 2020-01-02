package com.maoxin.apkshell.interceptor;

import android.content.Context;
import android.os.Bundle;

import com.alibaba.android.arouter.facade.Postcard;
import com.alibaba.android.arouter.facade.annotation.Interceptor;
import com.alibaba.android.arouter.facade.callback.InterceptorCallback;
import com.alibaba.android.arouter.facade.template.IInterceptor;

import javax.security.auth.login.LoginException;

/**
 * @author lmx
 * Created by lmx on 2020/1/2.
 */
@Interceptor(priority = 1, name = "ARouter拦截器")
public class MainInterceptor implements IInterceptor {
    @Override
    public void process(Postcard postcard, InterceptorCallback callback) {

        System.out.println(postcard);
        if (postcard == null) {
            callback.onInterrupt(new NullPointerException("postcard is null"));
        } else {
            Bundle extras = postcard.getExtras();
            if (extras == null || !extras.getBoolean("isLogin", false)) {
                callback.onInterrupt(new LoginException("is un login!!!"));
                return;
            }
            callback.onContinue(postcard);
        }
    }

    @Override
    public void init(Context context) {
        System.out.println(getClass().getSimpleName() + " : init[]");
    }
}
