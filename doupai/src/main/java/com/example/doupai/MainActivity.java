package com.example.doupai;

import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;
import doupai.venus.venus.Venus;

public class MainActivity extends AppCompatActivity
{
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Venus.init();

        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                //俄罗斯方块 ea1409e00b40082b17272b0F
                //透视墨镜 ea1B09E63c1AFC30033f2709
                //小黄鸭 ef1305431923eff712FD1B40
                //快闪 F212ff26022a381B42F1ffE7

                String key = "F212ff26022a381B42F1ffE7";

                ArrayList<Pair<String, String>> paths = new ArrayList<>();
                paths.add(new Pair<>("footage", Environment.getExternalStorageDirectory() + "/tencent/QQfile_recv/footage.json"));
                paths.add(new Pair<>("editable", Environment.getExternalStorageDirectory() + "/tencent/QQfile_recv/editable.json"));
                paths.add(new Pair<>("drawable", Environment.getExternalStorageDirectory() + "/tencent/QQfile_recv/drawable.json"));
                paths.add(new Pair<>("context", Environment.getExternalStorageDirectory() + "/tencent/QQfile_recv/context.json"));
                paths.add(new Pair<>("source", Environment.getExternalStorageDirectory() + "/tencent/QQfile_recv/source.json"));
                paths.add(new Pair<>("animation", Environment.getExternalStorageDirectory() + "/tencent/QQfile_recv/animation.zip"));
                paths.add(new Pair<>("maskshape", Environment.getExternalStorageDirectory() + "/tencent/QQfile_recv/maskshape.zip"));

                if (!TextUtils.isEmpty(key))
                {
                    for (Pair<String, String> path : paths)
                    {
                        String result = Venus.decrypt(path.second, key);
                        Log.i(TAG, "run: " +  path.first + " -----> " + result);
                    }
                }

                // try
                // {
                //     RandomAccessFile randomAccessFile = new RandomAccessFile(Environment.getExternalStorageDirectory() + "/tencent/QQfile_recv/animation.zip", "rws");
                //     byte[] read_byte = new byte[100];
                //     int read_index = randomAccessFile.read(read_byte);
                //     String string = new String(read_byte);
                //     System.out.println(string);
                // }
                // catch (Exception e)
                // {
                //     e.printStackTrace();
                // }
                //
                // String md5Key = "BHB";
                // byte[] bytes = EncryptKits.decryptAES(CommonUtils.ReadFile(Environment.getExternalStorageDirectory() + "/tencent/QQfile_recv/animation.zip"), md5Key);
                // if (bytes != null) {
                //     String s = new String(bytes);
                //     System.out.print(s);
                // }

                //加密一个文件
                // try
                // {
                //     File destFile = new File(Environment.getExternalStorageDirectory() + "/tencent/QQfile_recv/footage_encrypt.zip");
                //     ZipParameters par = new ZipParameters();
                //     par.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
                //     par.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);
                //     par.setEncryptFiles(true);
                //     par.setEncryptionMethod(Zip4jConstants.ENC_METHOD_STANDARD);
                //     par.setPassword("123456".toCharArray());
                //
                //     ZipFile zipFile = new ZipFile(destFile);
                //     zipFile.addFile(new File(Environment.getExternalStorageDirectory() + "/tencent/QQfile_recv/footage.json"), par);
                // }
                // catch (Exception e)
                // {
                //     e.printStackTrace();
                // }


            }
        }).start();
    }
}
