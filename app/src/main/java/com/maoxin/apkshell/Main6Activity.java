package com.maoxin.apkshell;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.maoxin.apkshell.gson.TeachLineLocalResDetial;
import com.maoxin.apkshell.utils.Zip;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;

public class Main6Activity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main6);

        try
        {
            String data = GetLocalAssetsRes(this, "test_teach_line.json");
            Gson gson = new Gson();
            Type type = new TypeToken<List<TeachLineLocalResDetial>>() {}.getType();
            List<TeachLineLocalResDetial> list = gson.fromJson(data, type);

            if (list != null)
            {
                String s = list.toString();
            }
        }
        catch (Throwable e)
        {
            e.printStackTrace();
        }

        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    Thread.sleep(2000);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
                String unzipFolder = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "apkshell" + File.separator + ".zip";
                File file = new File(unzipFolder);
                if (!file.exists()) {
                    boolean mkdirs = file.mkdirs();
                }
                String path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "apkshell" + File.separator + "test_zip.zip";
                try
                {
                    Zip.UnZipFolder(path, unzipFolder, true);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }

            }
        }).start();
    }

    public String GetLocalAssetsRes(Context context, String path)
    {
        InputStreamReader inputReader = null;
        BufferedReader reader = null;
        StringBuilder sb = null;
        try
        {
            InputStream is = context.getAssets().open(path);
            inputReader = new InputStreamReader(is);
            reader = new BufferedReader(inputReader);
            String inputLine;
            sb = new StringBuilder();
            while ((inputLine = reader.readLine()) != null)
            {
                sb.append(inputLine).append("\n");
            }
        }
        catch (Throwable e)
        {
            e.printStackTrace();
            sb = null;
        }
        finally
        {
            try
            {
                if (reader != null)
                {
                    reader.close();
                }
                if (inputReader != null)
                {
                    inputReader.close();
                }
            }
            catch (Throwable e1)
            {
                e1.printStackTrace();
            }
        }
        if (sb != null)
        {
            return sb.toString();
        }
        return null;
    }
}
