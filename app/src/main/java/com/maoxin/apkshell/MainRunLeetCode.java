package com.maoxin.apkshell;

import android.annotation.SuppressLint;

import java.util.Arrays;
import java.util.HashMap;

/**
 * @author lmx
 * Created by lmx on 2020/1/19.
 */
public class MainRunLeetCode {

    public static void main(String[] args) {
        int[] ints = test_twosum();
        System.out.println(Arrays.toString(ints));
    }


    /** 两数之和 */
    private static int[] test_twosum() {
        final int TARGET = 9;
        final int[] nums = new int[]{7, 8, 2, 11, 1};

        @SuppressLint("UseSparseArrays") HashMap<Integer, Integer> record = new HashMap<>();
        for (int i = 0; i < nums.length; i++) {
            record.put(nums[i], i);
        }
        for (int i = 0; i < nums.length; i++) {
            int offset = TARGET - nums[i];
            Integer getter =  record.get(offset);
            if (getter != null && getter != i) {
                return new int[]{i, getter};
            }
        }
        return new int[]{-1, -1};

    }

}
