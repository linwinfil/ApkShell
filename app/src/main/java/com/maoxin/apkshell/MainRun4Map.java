package com.maoxin.apkshell;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author lmx
 * Created by lmx on 2020/1/15.
 */
public class MainRun4Map {


    public static void main(String[] args) {
        // testHashMap();
        // System.out.println("---");

        // testLinkedHashMap();
        // System.out.println("---");

        testTreeMap();
    }


    private static void testTreeMap() {
        TreeMap<String, Integer> map = new TreeMap<>();
        map.remove("aac");

        map.put("aac", 1);
        map.put("aba", 2);
        map.put("dea", 3);
        map.put("casd", 4);
        map.put("gaaf", 5);
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            System.out.println(entry.getKey() + "=" + entry.getValue());
        }
    }

    private static void testHashMap() {
        HashMap<String, Integer> map = new HashMap<>();
        map.put("aac", 1);
        map.put("aba", 2);
        map.put("dea", 3);
        map.put("casd", 4);
        map.put("gaaf", 5);
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            System.out.println(entry.getKey() + "=" + entry.getValue());
        }
    }

    private static void testLinkedHashMap() {
        LinkedHashMap<String, Integer> map = new LinkedHashMap<>();
        map.put("aac", 1);
        map.put("aba", 2);
        map.put("dea", 3);
        map.put("casd", 4);
        map.put("gaaf", 5);
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            System.out.println(entry.getKey() + "=" + entry.getValue());
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            map.forEach((s, integer) -> {

            });
        }
    }
}
