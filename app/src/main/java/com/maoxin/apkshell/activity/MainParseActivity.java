package com.maoxin.apkshell.activity;

import android.app.ProgressDialog;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Toast;

import com.blankj.utilcode.util.FileIOUtils;
import com.blankj.utilcode.util.FileUtils;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import com.maoxin.apkshell.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Type;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.annotation.WorkerThread;
import androidx.appcompat.app.AppCompatActivity;

/**
 * @author lmx
 * Created by lmx on 2020/6/8.
 */
public class MainParseActivity extends AppCompatActivity {

    ProgressDialog progressBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_parse);
        progressBar = new ProgressDialog(this);

        findViewById(R.id.btn_parse).setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                progressBar.show();
                progressBar.setCancelable(false);
                new Thread(() -> {
                    try {
                        MainParseActivity.this.run();
                    } catch (Exception e) {
                        e.printStackTrace();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressBar.cancel();
                            }
                        });
                    }
                }).start();
            }
        });
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    @WorkerThread
    public void run() throws Exception {
        String root = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "apkshell/";
        List<Pair<Integer, String>> paths = new ArrayList<>();
        paths.add(new Pair<>(1, root + "布局 一张 jason"));
        paths.add(new Pair<>(2, root + "布局 两张 jason"));

        for (Pair<Integer, String> path : paths) {
            if (!FileUtils.isDir(path.second)) {
                throw new FileNotFoundException(path + " is not found");
            }
        }

        //对应比例下的排序
        Map<Integer, Map<String, List<Ratio>>> map = new LinkedHashMap<>();
        String layout_order_path = root + "puzzle_layout_order.json";
        parseLayoutOrder(layout_order_path, map);

        //总数据
        List<Data> out = new ArrayList<>();

        //id数据
        List<Integer> ids = new ArrayList<>();

        //解析
        for (Pair<Integer, String> path : paths) {
            if (!parse(map, ids, out, path.second, path.first)) {
                throw new IllegalStateException(path + " parse error");
            }
        }
        if (!out.isEmpty() && !map.isEmpty()) {
            Gson gson = new Gson();
            String str = gson.toJson(out);
            String filePath = root + "layout2.json";
            FileUtils.deleteDir(filePath);
            boolean b = FileIOUtils.writeFileFromString(filePath, str);

            gson = new Gson();
            String mapStr = gson.toJson(map);
            String ratioFilePath = root + "layout2_order.json";
            FileUtils.deleteDir(ratioFilePath);
            boolean b2 = FileIOUtils.writeFileFromString(ratioFilePath, mapStr);

            gson = new Gson();
            String idsStr = gson.toJson(ids);
            String idsFilePath = root + "layout2_ids.json";
            FileUtils.deleteDir(idsFilePath);
            boolean b3 = FileIOUtils.writeFileFromString(idsFilePath, idsStr);

            MainParseActivity.this.runOnUiThread(() -> {
                progressBar.cancel();
                if (b) {
                    String builder = new StringBuilder().append("success").append("\n order:").append(b2)
                            .append("\n").append("ids:").append(b3)
                            .append("\n").append("size:").append(out.size())
                            .append("\n").append(filePath).toString();
                    Toast.makeText(MainParseActivity.this, builder, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainParseActivity.this, "fail", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MainParseActivity.this, "fail size=0", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private boolean parse(Map<Integer, Map<String, List<Ratio>>> map,
                          List<Integer> ids, List<Data> dst, String path, int num) throws Exception {

        List<File> files = FileUtils.listFilesInDir(path);
        if (files.isEmpty()) return false;

        //对应比例下的排序
        Map<String, List<Ratio>> ratioMap;
        ratioMap = map.get(num);
        if (ratioMap == null) {
            ratioMap = new LinkedHashMap<>();
            map.put(num, ratioMap);
        }


        //排序
        Collator collator = Collator.getInstance(Locale.ENGLISH);
        Collections.sort(files, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                return collator.compare(o1.getName(), o2.getName());
            }
        });

        int order = 126;//顺序起始
        for (File file : files) {//1:1
            if (!file.isDirectory()) return false;
            //改比例下的所有素材json
            String name = file.getName();
            List<File> subFiles = FileUtils.listFilesInDir(file);
            //排序
            Collections.sort(subFiles, new Comparator<File>() {
                @Override
                public int compare(File o1, File o2) {
                    return collator.compare(o1.getName(), o2.getName());
                }
            });
            int index = 0;
            for (File subFile : subFiles) {//BasePuzzle80631
                List<File> fileList = FileUtils.listFilesInDir(subFile);
                for (File file1 : fileList) {
                    if (!file1.getName().endsWith(".json")) continue;//过滤非json数据
                    Data data = parse(file1, ++order, index, name, num);
                    dst.add(data);

                    //对应比例下的id
                    final String ratioStr = data.ratioStr;
                    List<Ratio> idList = ratioMap.get(ratioStr);
                    if (idList == null) {
                        ratioMap.put(ratioStr, idList = new ArrayList<>());
                    }
                    Ratio ratio = new Ratio();
                    ratio.id = data.file_tracking_id;
                    ids.add(Integer.parseInt(data.file_tracking_id));
                    idList.add(ratio);
                }
            }
        }


        return true;
    }

    private Data parse(File file, int order, int index, String ratioName, int sum) {
        String absolutePath = file.getAbsolutePath();
        String name = file.getName();
        long fileLength = FileUtils.getFileLength(file);
        String id = name.substring(0, name.lastIndexOf(".json")).replace("BasePuzzle", "");
        String ratioShuff = "x比x";
        String ratioStr = "";
        if (!TextUtils.isEmpty(ratioName)) {
            if (ratioName.equalsIgnoreCase("full")) {
                ratioShuff = "full";
                ratioStr = "full";
            } else {
                String[] split = ratioName.split("：");
                if (split.length == 2) {
                    ratioShuff = split[0] + "比" + split[1];
                    ratioStr = split[0] + ":" + split[1];
                }
            }
        }

        String readFile2String = FileIOUtils.readFile2String(absolutePath);
        try {
            JSONObject jsonObject = new JSONObject(readFile2String);
            if (!jsonObject.getJSONObject("point").has(Integer.toString(sum))) {
                Log.e("@@@", "point的数目不对：" + id);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Data data = new Data();
        data.ratioStr = ratioStr;

        data.file_tracking_id = id;
        data.relatid = "";
        //基础拼图-201803-9-d-4比3_3张
        data.name = "\u57fa\u7840\u62fc\u56fe" + "202006-8-a-" + ratioShuff + "_" + (index + 1) + "张" + "_specia";
        data.type = 0;
        data.subtype = "normal";
        data.restype = "\u57fa\u7840\u62fc\u56fe";
        data.restype_id = 11;
        data.sub_restype_id = 0;
        data.order = order;
        data.tracking_code = "0";
        data.tj_url = "";
        data.reddot_type = "";
        data.thumb_80 = id + ".jpg";
        data.thumb_120 = data.thumb_80;
        data.size = (int)Math.max(fileLength, 0);
        data.needFontId = "";
        data.needMusicId = "";
        data.measure = "";

        List<Data.Info> arr = new ArrayList<>();
        data.res_arr = arr;
        Data.Info info = new Data.Info();
        info.info = name;
        info.proportion = "square";
        info.maxPicNum = Integer.toString(sum);
        info.minPicNum = Integer.toString(sum);
        arr.add(info);

        return data;
    }

    private void parseLayoutOrder(String filePath, Map<Integer, Map<String, List<Ratio>>> map) {
        Gson gson = new Gson();
        String file2String = FileIOUtils.readFile2String(filePath);
        Type type = new TypeToken<LinkedHashMap<Integer, LinkedHashMap<String, ArrayList<Ratio>>>>() {}.getType();
        Object o = gson.fromJson(file2String, type);
        if (o instanceof Map) {
            Map<Integer, Map<String, List<Ratio>>> temp = (Map<Integer, Map<String, List<Ratio>>>) o;
            map.putAll(temp);
        }
    }
    private static class Ratio {
        @SerializedName("id") public String id;
    }

    private static class Data {
        /**
         * {
         * "file_tracking_id": "25101",
         * "relatid": "",
         * "name": "\u57fa\u7840\u62fc\u56fe-201803-9-d-4\u6bd43_3",
         * "type": 0,
         * "subtype": "normal",
         * "restype": "\u57fa\u7840\u62fc\u56fe",
         * "restype_id": 11,
         * "sub_restype": "",
         * "sub_restype_id": 0,
         * "order": 126,
         * "tracking_code": "1206118964",
         * "tj_url": "",
         * "reddot_type": "",
         * "thumb_80": "25101.jpg",
         * "thumb_120": "25101.jpg",
         * "size": 10140,
         * "needFontId": "",
         * "needMusicId": "",
         * "measure": "",
         * "res_arr": [
         * {
         * "info": "BasePuzzle25101.json",
         * "proportion": "square",
         * "maxPicNum": "9",
         * "minPicNum": "9"
         * }
         * ]
         * }
         */

        @SerializedName("file_tracking_id")
        public String file_tracking_id;
        @SerializedName("relatid")
        public String relatid;
        @SerializedName("name")
        public String name;
        @SerializedName("type")
        public int type;
        @SerializedName("subtype")
        public String subtype;
        @SerializedName("restype")
        public String restype;
        @SerializedName("restype_id")
        public int restype_id;
        @SerializedName("sub_restype")
        public String sub_restype;
        @SerializedName("sub_restype_id")
        public int sub_restype_id;
        @SerializedName("order")
        public int order;
        @SerializedName("tracking_code")
        public String tracking_code;
        @SerializedName("tj_url")
        public String tj_url;
        @SerializedName("reddot_type")
        public String reddot_type;
        @SerializedName("thumb_80")
        public String thumb_80;
        @SerializedName("thumb_120")
        public String thumb_120;
        @SerializedName("size")
        public int size;
        @SerializedName("needFontId")
        public String needFontId;
        @SerializedName("needMusicId")
        public String needMusicId;
        @SerializedName("measure")
        public String measure;
        @SerializedName("res_arr")
        public List<Info> res_arr;

        public transient String ratioStr;

        public static class Info {
            @SerializedName("info")
            public String info;
            @SerializedName("proportion")
            public String proportion;
            @SerializedName("maxPicNum")
            public String maxPicNum;
            @SerializedName("minPicNum")
            public String minPicNum;
        }
    }

    private static class Bean {
        @SerializedName("minPicNum") public String minPicNum;
        @SerializedName("maxPicNum") public String maxPicNum;
        @SerializedName("point") public Point point;

    }
}
