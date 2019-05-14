package com.example.opengl.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.Menu;
import android.view.MenuItem;

import com.example.opengl.R;

import java.io.InputStream;
import java.net.URL;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * @author lmx
 * Created by lmx on 2019/5/14.
 */
public abstract class BaseActivity extends AppCompatActivity
{
    public static final int REQUEST_PICK_IMAGE = 1;


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.gl_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.getItemId() == R.id.mPicker)
        {
            Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
            photoPickerIntent.setType("image/*");
            this.startActivityForResult(photoPickerIntent, REQUEST_PICK_IMAGE);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        if (requestCode == REQUEST_PICK_IMAGE)
        {
            if (resultCode == Activity.RESULT_OK && data != null)
            {
                handleImage(data.getData());
                return;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void handleImage(final Uri selectedImage)
    {
        new LoadImageTask(this, selectedImage, this::handleImageCallback).execute();
    }

    public abstract void handleImageCallback(Bitmap bitmap);

    private static class LoadImageTask extends AsyncTask<Void, Void, Bitmap>
    {

        public interface CallBack
        {
            void callback(Bitmap bitmap);
        }

        public Uri mUri;
        public Context mContext;
        public CallBack mCallBack;

        public LoadImageTask(Context mContext, Uri mUri, CallBack mCallBack)
        {
            this.mContext = mContext;
            this.mUri = mUri;
            this.mCallBack = mCallBack;
        }

        @Override
        protected Bitmap doInBackground(Void... voids)
        {
            try
            {
                InputStream inputStream;
                if (mUri.getScheme().startsWith("http") || mUri.getScheme().startsWith("https"))
                {
                    inputStream = new URL(mUri.toString()).openStream();
                }
                else
                {
                    inputStream = mContext.getContentResolver().openInputStream(mUri);
                }
                return BitmapFactory.decodeStream(inputStream, null, null);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap)
        {
            super.onPostExecute(bitmap);
            if (mCallBack != null) mCallBack.callback(bitmap);
        }
    }
}
