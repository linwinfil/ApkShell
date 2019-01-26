package com.maoxin.apkshell;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.text.InputType;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.coremedia.iso.IsoFile;
import com.maoxin.apkshell.audio.FileUtils;
import com.maoxin.apkshell.utils.MLog;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import androidx.appcompat.app.AppCompatActivity;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.poco.audio.CommonUtils;

public class Main5Activity extends AppCompatActivity implements MLog.ILogTag
{

    public static final String VIDEO_SEPARATOR = File.separator + "video" + File.separator;
    public static final String AUDIO_SEPARATOR = File.separator + "audio" + File.separator;

    private static final String TAG = "Main5Activity";

    public static final String ratio9_16 = "ratio_9_16";
    public static final String ratio16_9 = "ratio_16_9";
    public static final String ratio3_4 = "ratio_3_4";
    public static final String ratio_9_16_180s = "ratio_9_16_180s";

    private HashMap<String, ArrayList<String>> videoPaths = new HashMap<>();

    private ArrayList<String> musicPaths = new ArrayList<>();

    String video_basePath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "apkshell" + VIDEO_SEPARATOR;
    String audio_basePath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "apkshell" + AUDIO_SEPARATOR;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main5);
        ButterKnife.bind(this);

    }


    @OnClick({R.id.ratio9_16, R.id.ratio16_19, R.id.ratio3_4, R.id.ratio9_16_180s})
    public void onClickMp4Append(View view)
    {
        int id = view.getId();
        String compileKey = "";
        if (id == R.id.ratio9_16)
        {
            compileKey = ratio9_16;
        }
        else if (id == R.id.ratio16_19)
        {
            compileKey = ratio16_9;
        }
        else if (id == R.id.ratio3_4)
        {
            compileKey = ratio3_4;
        }
        else if (id == R.id.ratio9_16_180s)
        {
            compileKey = ratio_9_16_180s;
        }

        ArrayList<String> mp4path = null;
        Set<Map.Entry<String, ArrayList<String>>> entrySet = videoPaths.entrySet();
        for (Map.Entry<String, ArrayList<String>> entry : entrySet)
        {
            String key = entry.getKey();
            if (!TextUtils.isEmpty(key) && compileKey.equals(key))
            {
                mp4path = entry.getValue();
                break;
            }
        }

        if (mp4path == null) return;

        long start = System.currentTimeMillis();
        String out = null;
        try
        {
            out = video_basePath + "mp4parse.mp4";
            FileUtils.delete(out);
            Mp4ParseUtils.appendMp4List(mp4path, out);
        }
        catch (Throwable e)
        {
            e.printStackTrace();
        }
        MLog.d(this, String.format("mp4 parser %s", (System.currentTimeMillis() - start)));
    }

    @OnClick(R.id.ratio_add)
    public void onClickMp4RatioAdd()
    {
        ArrayList<String> mp4path = new ArrayList<>();
        for (ArrayList<String> strings : videoPaths.values())
        {
            mp4path.addAll(strings);
        }

        long start = System.currentTimeMillis();
        String out = null;
        try
        {
            out = video_basePath + "mp4parse.mp4";
            FileUtils.delete(out);
            Mp4ParseUtils.appendMp4List(mp4path, out);
        }
        catch (Throwable e)
        {
            e.printStackTrace();
        }
        MLog.d(this, String.format("mp4 parser %s", (System.currentTimeMillis() - start)));
    }

    @OnClick(R.id.muxer_video_audio)
    public void onClickMuxerVideoAudio()
    {
        String videoPath = video_basePath + "mp4parse.mp4";
        if (!FileUtils.isFileExists(videoPath))
        {
            Toast.makeText(this, "视频不在", Toast.LENGTH_SHORT).show();
            return;
        }
    }

    @OnClick(R.id.extract_audio)
    public void onClickExtractAudio()
    {
        //可行
        ArrayList<String> mp4path = null;
        /*Set<Map.Entry<String, ArrayList<String>>> entrySet = videoPaths.entrySet();
        for (Map.Entry<String, ArrayList<String>> entry : entrySet)
        {
            String key = entry.getKey();
            if (!TextUtils.isEmpty(key) && ratio9_16.equals(key))
            {
                mp4path = entry.getValue();
                break;
            }
        }*/

        //多个不同视频比例可以追加
        mp4path = new ArrayList<>();
        for (ArrayList<String> strings : videoPaths.values())
        {
            mp4path.addAll(strings);
        }

        if (mp4path == null) return;

        long start = System.currentTimeMillis();
        String out = null;
        try
        {
            out = audio_basePath + "extract_audio.aac";
            FileUtils.delete(out);
            Mp4ParseUtils.extractAudio(mp4path, out);
        }
        catch (Throwable e)
        {
            e.printStackTrace();
        }
        MLog.d(this, String.format("extract_audio %s", (System.currentTimeMillis() - start)));
    }


    @OnClick(R.id.extract_video)
    public void onClickExtractVideo()
    {
        ArrayList<String> mp4path = null;
        Set<Map.Entry<String, ArrayList<String>>> entrySet = videoPaths.entrySet();
        for (Map.Entry<String, ArrayList<String>> entry : entrySet)
        {
            String key = entry.getKey();
            if (!TextUtils.isEmpty(key) && ratio9_16.equals(key))
            {
                mp4path = entry.getValue();
                break;
            }
        }
        if (mp4path == null) return;

        long start = System.currentTimeMillis();
        String out = null;
        try
        {
            out = video_basePath + "extract_video.mp4";
            FileUtils.delete(out);
            Mp4ParseUtils.extractVideo(mp4path, out);
        }
        catch (Throwable e)
        {
            e.printStackTrace();
        }
        MLog.d(this, String.format("extract_video %s", (System.currentTimeMillis() - start)));
    }

    @OnClick(R.id.get_duration)
    public void onClickGetDuration()
    {
        String path = video_basePath + "mp4parse.mp4";
        if (!FileUtils.isFileExists(path))
        {
            Toast.makeText(Main5Activity.this, "无视频", Toast.LENGTH_SHORT).show();
            return;
        }
        try
        {
            IsoFile isoFile = new IsoFile(path);
            long duration = isoFile.getMovieBox().getMovieHeaderBox().getDuration();
            long timescale = isoFile.getMovieBox().getMovieHeaderBox().getTimescale();
            double lengthInSeconds = (double) duration / timescale;
            long mmDuration = Mp4ParseUtils.getDuration(path);
            MLog.d(this, String.format("duration:%s, timescale:%s, lengthInSeconds:%s, mm_duration:%s", duration, timescale, lengthInSeconds, mmDuration));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

    }

    @OnClick(R.id.get_frame_rate)
    public void onClickGetFPS()
    {
        ArrayList<String> strings = videoPaths.get(ratio_9_16_180s);
        if (strings != null && strings.size() > 0)
        {
            String s = strings.get(0);
            try
            {
                long start = System.currentTimeMillis();
                float videoFrameRate = Mp4ParseUtils.getVideoFrameRate(s);
                MLog.d(this, String.format("get fps, %s, time:%s", videoFrameRate, (System.currentTimeMillis() - start)));

            }
            catch (Throwable e)
            {
                e.printStackTrace();
            }
        }
    }

    @OnClick(R.id.volume_adjust)
    public void onClickVolumeAdjust()
    {
        final EditText editText = new EditText(this);
        editText.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
        FrameLayout.LayoutParams fl = new FrameLayout.LayoutParams((int) (240 * 1.5), FrameLayout.LayoutParams.WRAP_CONTENT);
        fl.gravity = Gravity.CENTER;
        editText.setLayoutParams(fl);

        AlertDialog dlg = new AlertDialog.Builder(this).create();
        dlg.setTitle("输入");
        dlg.setView(editText);
        dlg.setButton(AlertDialog.BUTTON_POSITIVE, "确定", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                String keyword = editText.getText().toString();
                float volume = 1.0f;
                try { volume = Float.valueOf(keyword); } catch (Throwable t){}
                if (volume > 1.0f) volume = 1.0f;

                ArrayList<String> strings = videoPaths.get(ratio_9_16_180s);
                if (strings != null && strings.size() > 0) {
                    String out = video_basePath + "volume_adjust_out.mp4";
                    try
                    {
                        long start = System.currentTimeMillis();
                        Mp4ParseUtils.setVolume(strings.get(0), out, volume);
                        MLog.d(Main5Activity.this, String.format("volume_adjust: %s", (System.currentTimeMillis() - start)));
                    }
                    catch (Throwable throwable)
                    {
                        throwable.printStackTrace();
                    }
                }
            }
        });
        dlg.setButton(AlertDialog.BUTTON_NEGATIVE, "取消", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
            }
        });
        dlg.show();
    }

    @OnClick(R.id.get_extra_video_audio)
    public void onClickExtractVideoAudio()
    {
        ArrayList<String> strings = videoPaths.get(ratio_9_16_180s);

        String mp4Path = video_basePath + "extract_ratio_9_16_180s.mp4";
        String aacPath = audio_basePath + "extract_ratio_9_16_180s.aac";

        strings = new ArrayList<>(strings);
        strings.remove(0);

        try
        {
            long start = System.currentTimeMillis();
            Mp4ParseUtils.extractVideoAndAudio(strings, mp4Path, aacPath);
            MLog.d(this, String.format("get_extra_video_audio, time:%s", (System.currentTimeMillis() - start)));
        }
        catch (Throwable throwable)
        {
            throwable.printStackTrace();
        }
    }


    @OnClick(R.id.muxer_same_ratio_video)
    public void onClickMuxerSameVideo()
    {
        ArrayList<String> mp4path = new ArrayList<>();
        ArrayList<String> strings = videoPaths.get(ratio_9_16_180s);
        mp4path.addAll(strings);

        ArrayList<String> strings1 = videoPaths.get(ratio9_16);
        mp4path.addAll(strings1);

        long start = System.currentTimeMillis();
        try
        {
            String out = video_basePath + "mp4parse.mp4";
            Mp4ParseUtils.appendMp4List(mp4path, out);
        }
        catch (Throwable throwable)
        {
            throwable.printStackTrace();
        }
        MLog.d(this, String.format("mp4 parser %s", (System.currentTimeMillis() - start)));
    }

    @OnClick(R.id.get_audio_sample_rate_by_jni)
    public void onClickGetAudioSampleRate()
    {

        try
        {
            ArrayList<String> strings = videoPaths.get(ratio3_4);
            if (strings != null && strings.size() > 0)
            {
                String path = strings.get(0);
                String outpath = audio_basePath + "get_audio_sample_rate_by_jni.aac";
                Mp4ParseUtils.extractAudio(Collections.singletonList(path), outpath);
                int audioSampleRate = CommonUtils.getAudioSampleRate(outpath);
                int audioChannels = CommonUtils.getAudioChannels(outpath);
                MLog.d(this, String.format("sample_rate: %s, channel: %s", audioSampleRate, audioChannels));
            }
        }
        catch (Throwable throwable)
        {
            throwable.printStackTrace();
        }
    }

    public void loadVideoSource()
    {
        //1-5 9_16格式
        //6-10 16_9格式
        //10-15 3_4格式
        //16-17 三分钟9_16格式
        ArrayList<String> path = null;
        for (int i = 0; i < 17; i++)
        {
            if (i >= 0 && i < 5)
            {
                if (i == 0) path = new ArrayList<>();

                path.add(video_basePath + String.format("%s.mp4", i + 1));
                if (i == 5 - 1)
                {
                    videoPaths.put(ratio9_16, path);
                }
            }
            else if (i >= 5 && i < 10)
            {
                if (i == 5) path = new ArrayList<>();
                path.add(video_basePath + String.format("%s.mp4", i + 1));
                if (i == 10 - 1)
                {
                    videoPaths.put(ratio16_9, path);
                }
            }
            else if (i >= 10 && i < 15)
            {
                if (i == 10) path = new ArrayList<>();
                path.add(video_basePath + String.format("%s.mp4", i + 1));
                if (i == 15 - 1)
                {
                    videoPaths.put(ratio3_4, path);
                }
            }
            else if (i >= 15 && i < 17)
            {
                if (i == 15) path = new ArrayList<>();
                path.add(video_basePath + String.format("%s.mp4", i + 1));
                if (i == 17 - 1)
                {
                    videoPaths.put(ratio_9_16_180s, path);
                }
            }
        }

        musicPaths.add(audio_basePath + "CANNIE一万次悲伤.mp3");
    }

    @Override
    public String getLogTag()
    {
        return TAG;
    }
}
