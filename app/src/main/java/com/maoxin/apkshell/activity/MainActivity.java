package com.maoxin.apkshell.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.maoxin.apkshell.R;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener
{
    private static final String TAG = "MainActivity";

    private static final String X_BREVENT = "shell:sh /data/data/me.piebridge.brevent/brevent.sh";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.button).setOnClickListener(this);
    }

    @Override
    public void onClick(View view)
    {
        if (view.getId() == R.id.button)
        {
            try
            {
                Process process = Runtime.getRuntime().exec("sh");
                DataOutputStream dataOutputStream = new DataOutputStream(process.getOutputStream());
                dataOutputStream.write(formatBytes(X_BREVENT));
                dataOutputStream.flush();

                int result = process.waitFor();

                //读取执行信息
                StringBuilder successMsg = new StringBuilder();
                StringBuilder errorMsg = new StringBuilder();
                BufferedReader successResult = new BufferedReader(new InputStreamReader(process.getInputStream()));
                BufferedReader errorResult = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                String s;
                while ((s = successResult.readLine()) != null)
                {
                    successMsg.append(s);
                }
                while ((s = errorResult.readLine()) != null)
                {
                    errorMsg.append(s);
                }

                //关闭流操作
                dataOutputStream.close();
                successResult.close();
                errorResult.close();

                Log.d(TAG, "MainActivity --> onClick: ss " + successMsg + "\nee " + errorMsg + result);

            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }


    private byte[] formatBytes(String s) throws UnsupportedEncodingException
    {
        byte[] bytes = s.getBytes("UTF-8");
        return Arrays.copyOf(bytes, bytes.length + 1);
    }

}
