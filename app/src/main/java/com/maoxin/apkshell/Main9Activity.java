package com.maoxin.apkshell;

import android.Manifest;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.VideoView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

public class Main9Activity extends AppCompatActivity
{
    public static class MyFileProvider extends FileProvider
    {
    }

    private static final int RECORD_VIDEO = 1;
    private static final int TAKE_PHOTO = 2;
    private static final int SELECT_PHOTO = 3;
    private static final int EDIT = 4;

    private static final int REQUEST_PERMISSION_CODE = 0x112;

    private FrameLayout mViewFr;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main9);

        mViewFr = findViewById(R.id.root_view);

        new Handler(Looper.getMainLooper()).postDelayed(() ->
        {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSION_CODE);
        }, 500);
    }

    public void onClick_home(View view)
    {
        startAction("camera21://page?open=8");
    }

    public void onClick_photo(View view)
    {
        startAction("camera21://page?open=0&type=photo");
    }

    public void onClick_video(View view)
    {
        startAction("camera21://page?open=0&type=video");
    }

    public void onclick_camera_cartoon(View view)
    {
        startAction("camera21://page?open=0&filter=1000006");
    }

    public void onClick_edit(View view)
    {
        startAction("camera21://page?open=9");
    }

    public void onClick_gonglue(View view)
    {
        startAction("camera21://page?open=3");
    }

    public void onClick_tiaozhan(View view)
    {
        startAction("camera21://page?open=2");
    }

    public void onclick_tiaozhan_huodong(View view)
    {
        startAction("camera21://page?open=2&id=1298");
    }

    public void onclick_social(View view)
    {
        startAction("camera21://page?open=7");
    }

    public void onclick_filter_manager(View view)
    {
        startAction("camera21://page?open=6");
    }

    public void onClick_extend_record(View view)
    {
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        startActivityForResult(intent, RECORD_VIDEO);
    }

    public void onclick_extend_photo(View view)
    {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        Uri photoUri = getImageOutPutMediaFileUri();
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
        startActivityForResult(intent, TAKE_PHOTO);
    }

    public void onclick_select_photo_edit(View view)
    {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, SELECT_PHOTO);
    }

    public void onclick_mr_open(View view)
    {
       startAction("beautycameralink://goto/pagepath?pageid=33");
    }

    protected void startAction(String url)
    {
        try
        {
            Uri uri = Uri.parse(url);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            this.startActivity(intent);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private String getPath(Uri uri)
    {
        String out = null;
        Cursor cursor = getContentResolver().query(uri, new String[]{MediaStore.Video.Media.DATA}, null, null, null);
        if (cursor != null)
        {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
            if (column_index > -1)
            {
                cursor.moveToFirst();
                out = cursor.getString(column_index);
            }
            cursor.close();
        }
        return out;
    }

    public String mImgPath;

    public Uri getImageOutPutMediaFileUri()
    {
        File mediaStorageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsoluteFile();
        if (!mediaStorageDir.exists())
        {
            if (!mediaStorageDir.mkdirs())
            {
                return null;
            }
        }
        //创建Media File
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(new Date());
        mImgPath = mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg";
        File mediaFile = new File(mImgPath);
        // TODO: 2018/6/27
        if (Build.VERSION.SDK_INT >= 24)
        {
            return FileProvider.getUriForFile(this, getApplicationInfo().packageName + ".myfileprovider", mediaFile);
        }
        else
        {
            return Uri.fromFile(mediaFile);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK)
        {
            switch (requestCode)
            {
                case TAKE_PHOTO:
                {
                    mViewFr.removeAllViews();

                    ImageView view = new ImageView(this);
                    FrameLayout.LayoutParams fl = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
                    fl.gravity = Gravity.CENTER;
                    mViewFr.addView(view, fl);

                    if (data.hasExtra("data"))
                    {
                        Bitmap bitmap = data.getParcelableExtra("data");
                        view.setImageBitmap(bitmap);
                    }
                    else
                    {
                        // TODO: 2018/6/27
                        if (Build.VERSION.SDK_INT >= 24)
                        {
                            try
                            {
                                Uri uri = data.getData();
                                if (uri != null)
                                {
                                    Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
                                    view.setImageBitmap(bitmap);
                                }
                            }
                            catch (Throwable e)
                            {
                                e.printStackTrace();
                            }
                        }
                        else
                        {
                            Bitmap bitmap = BitmapFactory.decodeFile(mImgPath);
                            view.setImageBitmap(bitmap);
                        }
                    }

                    break;
                }
                case RECORD_VIDEO:
                {
                    mViewFr.removeAllViews();

                    System.out.println(getPath(data.getData()));
                    System.out.println("ok");

                    final VideoView view = new VideoView(this);
                    FrameLayout.LayoutParams fl = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
                    fl.gravity = Gravity.CENTER;
                    mViewFr.addView(view, fl);
                    view.setOnTouchListener((v, event) ->
                    {
                        if (event.getPointerCount() == 1 && event.getAction() == MotionEvent.ACTION_UP)
                        {
                            view.start();
                        }
                        return true;
                    });
                    view.setVideoURI(data.getData());
                    view.start();
                    break;
                }
                case SELECT_PHOTO:
                {
                    Uri selectImageUri = data.getData();
                    if (selectImageUri != null)
                    {
                        String[] filePathColumn = {MediaStore.Images.Media.DATA};
                        Cursor cursor = getContentResolver().query(selectImageUri, filePathColumn, null, null, null);
                        if (cursor != null)
                        {
                            cursor.moveToFirst();
                            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                            String picturePath = cursor.getString(columnIndex);
                            cursor.close();

                            //去编辑
                            Intent intent = new Intent(Intent.ACTION_EDIT);
                            // TODO: 2018/6/27
                            if (Build.VERSION.SDK_INT >= 24)
                            {
                                intent.setDataAndType(FileProvider.getUriForFile(this, getApplicationInfo().packageName + ".myfileprovider", new File(picturePath)), "image/*");
                                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            }
                            else
                            {
                                intent.setDataAndType(Uri.fromFile(new File(picturePath)), "image/*");
                            }
                            startActivityForResult(intent, EDIT);
                        }
                    }
                    break;
                }
                case EDIT:
                {
                    mViewFr.removeAllViews();

                    ImageView view = new ImageView(this);
                    FrameLayout.LayoutParams fl = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
                    fl.gravity = Gravity.CENTER;
                    mViewFr.addView(view, fl);

                    Uri uri = data.getData();
                    view.setImageURI(uri);
                    break;
                }
            }
        }
    }

}
