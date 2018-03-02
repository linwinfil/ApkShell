package com.maoxin.apkshell;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.JsonReader;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.maoxin.apkshell.audio.ClipMusicTask;
import com.maoxin.apkshell.audio.FileUtils;
import com.maoxin.apkshell.audio.OnProcessListener;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class Main3Activity extends AppCompatActivity
{
    private static final String TAG = "Main3Activity";

    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        recyclerView = findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setSmoothScrollbarEnabled(true);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);


        TestListLoader listLoader = new TestListLoader();
        listLoader.execute();
    }

    private void postRun(Runnable runnable)
    {
        if (runnable != null)
        {
            new Thread(runnable).start();
        }
    }

    View.OnClickListener clickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            TextView textView = (TextView) v;
            String title = textView.getText().toString();
            String path = FileUtils.GetAppPath() + "/audio/" + title;

            if (!FileUtils.isFileExists(path)) return;

            final String outTitle = title.split("\\.")[0] + "_test.aac";
            String outputPath = FileUtils.GetAppPath() + File.separator + outTitle;

            if (FileUtils.isFileExists(outputPath)) {
                FileUtils.delete(outputPath);
            }

            ClipMusicTask clipMusicTask = new ClipMusicTask(path, 0, 60 * 1000L, outputPath);
            clipMusicTask.setOnProcessListener(new OnProcessListener()
            {
                long start;
                long before_duration;

                @Override
                public void onStart()
                {
                    start = System.currentTimeMillis();
                }

                @Override
                public void onFinish()
                {
                    Log.d(TAG, "Main3Activity --> onFinish: " + outTitle + ", " + (System.currentTimeMillis() - start));
                }

                @Override
                public void onError(String message)
                {

                }
            });
            postRun(clipMusicTask);
        }
    };


    public final class TestListLoader extends AsyncTask<Void, Void, ArrayList<String>>
    {

        @Override
        protected ArrayList<String> doInBackground(Void... voids)
        {
            ArrayList<String> out = new ArrayList<>();

            AssetManager assetManager = getAssets();
            InputStream inputStream = null;
            try
            {
                inputStream = assetManager.open("test_audio.json");
                JsonReader jsonReader = new JsonReader(new InputStreamReader(inputStream, "UTF-8"));
                jsonReader.beginArray();
                while (jsonReader.hasNext())
                {
                    jsonReader.beginObject();
                    while (jsonReader.hasNext())
                    {
                        switch (jsonReader.nextName())
                        {
                            case "name":
                                out.add(jsonReader.nextString());
                                break;
                        }
                    }
                    jsonReader.endObject();
                }
                jsonReader.endArray();

            }
            catch (Throwable e)
            {
            }
            finally
            {
                if (inputStream != null)
                {
                    try
                    {
                        inputStream.close();
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
            return out;
        }

        @Override
        protected void onPostExecute(ArrayList<String> strings)
        {
            if (strings == null) throw new IllegalStateException("string is null");
            RCAdapter adapter = new RCAdapter(Main3Activity.this, clickListener);
            adapter.data = strings;
            recyclerView.setAdapter(adapter);
        }
    }

    public static class RCAdapter extends RecyclerView.Adapter<ViewHolder>
    {
        ArrayList<String> data;
        Context context;

        View.OnClickListener clickListener;

        public RCAdapter(Context context, final View.OnClickListener listener)
        {
            this.context = context;
            clickListener = new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if (listener != null)
                    {
                        listener.onClick(v);
                    }
                }
            };
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {
            TextView textView = new TextView(context);
            textView.setGravity(Gravity.CENTER);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
            textView.setPadding(0, 50, 0, 50);
            RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT);
            textView.setLayoutParams(layoutParams);
            return new ViewHolder(textView);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position)
        {
            TextView itemView = (TextView) holder.itemView;
            itemView.setText(data.get(position));
            itemView.setTag(position);
            itemView.setOnClickListener(clickListener);
        }

        @Override
        public int getItemCount()
        {
            return data == null ? 0 : data.size();
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder
    {

        public ViewHolder(View itemView)
        {
            super(itemView);
        }
    }
}
