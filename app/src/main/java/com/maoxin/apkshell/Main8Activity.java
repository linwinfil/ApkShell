package com.maoxin.apkshell;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Main8Activity extends AppCompatActivity
{
    private static final String TAG = "Main8Activity";

    private volatile ConcurrentHashMap<String, ConcurrentLinkedQueue<TestBean>> mDataArr;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main8);
        mDataArr = new ConcurrentHashMap<>();

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                runTest();
            }
        }, 600);
    }

    private void runTest()
    {
        newThread(new Runnable()
        {
            @Override
            public void run()
            {
                for (int i = 0; i < 20; i++)
                {
                    TestBean testBean = new TestBean();
                    testBean.i = i;
                    if (i < 10)
                    {
                        testBean.type = "A";
                        add(testBean, "A");
                    }
                    else
                    {
                        testBean.type = "B";
                        add(testBean, "B");
                    }
                }
            }
        }).start();

        newThread(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    Thread.sleep(5);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }

                asyncData("A");
            }
        }).start();

        newThread(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    Thread.sleep(100);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }

                for (int i = 20; i < 30; i++)
                {
                    TestBean testBean = new TestBean();
                    testBean.i = i;
                    testBean.type = "A";
                    add(testBean, "A");
                }

                asyncData("A");
            }
        }).start();
    }

    private Thread newThread(Runnable runnable)
    {
        return new Thread(runnable);
    }


    private void add(TestBean bean, String key)
    {
        ConcurrentLinkedQueue<TestBean> arr = mDataArr.get(key);
        if (arr == null) {
            arr = new ConcurrentLinkedQueue<>();
            mDataArr.put(key, arr);
        }
        bean.adding();
        arr.add(bean);
    }

    private void asyncData(@NonNull final String key)
    {
        if (mDataArr.size() > 0 && mDataArr.containsKey(key))
        {
            new Thread(new Runnable()
            {
                @Override
                public void run()
                {
                    ConcurrentLinkedQueue<TestBean> arr = mDataArr.get(key);
                    if (arr != null)
                    {
                        while (!arr.isEmpty())
                        {
                            TestBean poll = arr.poll();
                            if (poll != null)
                            {
                                poll.dosomething();
                            }
                        }
                        Log.d(TAG, "asyncData key: " + key + ", lest size: " + arr.size());
                    }
                }
            }).start();
        }
    }


    private static final class TestBean
    {
        String type;
        int i;

        public void dosomething()
        {
            Log.d(TAG, "type : " + type + ", i === " + i + ", do something");

        }

        public void adding()
        {
            Log.d(TAG, "type : " + type + ", i === " + i + ", adding");
        }
    }
}
