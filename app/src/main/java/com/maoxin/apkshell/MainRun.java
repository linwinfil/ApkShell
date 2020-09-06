package com.maoxin.apkshell;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.Semaphore;

/**
 * @author lmx
 * Created by lmx on 2020/2/25.
 */
public class MainRun {

    public static void main(String[] args) {
        // System.out.println(getNum(100));


        // runFutureTask();
        // String[] a = new String[10];
        // for (String s : a) {
        //
        // }

        //计算大数
        String a = "123456789098765432112345622232323232323";
        String a2 = "1234567890987654321123456222292929999999";
        BigDecimal bigDecimal = new BigDecimal(a);
        BigDecimal bigDecimal2 = new BigDecimal(a2);
        BigDecimal add = bigDecimal.add(bigDecimal2);
        System.out.println(add);


        // runSemaphore();
        // runLengthOfLongestSubstring("abcabcbb");


        // MLRUCache<Integer, String> cache = new MLRUCache<>();
        // cache.put(3, "a");
        // cache.put(5, "b");
        // cache.put(7, "c");
        // cache.get(5);
        // cache.put(9, "d");
        // cache.put(11, "e");
        // cache.get(3);
        // cache.put(12, "gg");
        // System.out.println(cache.toString());


        // 逆序数组
        int[] arr = new int[]{1, 2, 3, 4, 5, 6};
        runReverseArray(arr);
        System.out.println(Arrays.toString(arr));

    }


    //使用length和charAt匹配是否包含
    private static void runContains() {
        String target = "adbslchjalsdasd";//target
        String source = "sbhgjcjiaadbslchjalsdasdsashjds";//source
        // boolean contains = source.contains(target);
        boolean out  = false;

        int target_len = target.length();
        int source_len = source.length();
        int max = source_len - target_len;

        char first = target.charAt(0);
        int fromIndex = 0;
        for (int i = fromIndex; i <= max; i++) {
            //寻找第一个char是否匹配
            if (source.charAt(i) != first) {
                //第一个char不匹配，则循环source下标
                while(++i <= max && source.charAt(i) != first);
            }

            //匹配到了开头
            if (i <= max) {
                int j = i + 1;//source下标+1
                int end = j + target_len -1;
                //k：寻找target的索引，从1开始
                for (int k = 1;
                     j < end && source.charAt(j) == target.charAt(k);
                     j++, k++);
                if (j == end) {
                    out = true;
                    break;
                }
            }
        }
        System.out.println(out);
    }

    private static void runReflet(String[] args) {
        if (args == null || args.length == 0) return;
        try {
            Class<?> aClass = Class.forName(args[0]);
            Object o = aClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void runReverseArray(int[] arr) {
        for (int start = 0, end = arr.length - 1; start < end; start++, end--) {
            swap(arr, start, end);
        }
    }

    private static void swap(int[] arr, int a, int b) {
        int temp = arr[a];
        arr[a] = arr[b];
        arr[b] = temp;
    }

    private static class MLRUCache<K, V> extends LinkedHashMap<K, V> {
        private static final int MAX_ENTRIES = 4;

        protected boolean removeEldestEntry(Map.Entry eldest) {
            return size() > MAX_ENTRIES;
        }

        MLRUCache() {
            super(MAX_ENTRIES, 0.75f, true);
        }
    }

    private static int runLengthOfLongestSubstring(String s) {
        int result = 0;
        if (s != null && s.length() != 0) {
            int flag = 0;
            for (int i = 0, length = s.length(); i < length; i++) {
                char charAt = s.charAt(i);
                if ((flag & charAt) == charAt) {
                    result = 0;
                    continue;
                }
                flag = flag | charAt;
                result += 1;
            }
        }
        result += 1;
        System.out.println(result);
        return result;
    }

    private static void runFutureTask() {
        ExecutorService executorService = Executors.newCachedThreadPool();

        FutureTask<Object> futureTask = new FutureTask<Object>(() -> {
            //doing something
            printIsMain();
            try {
                Thread.sleep(5_000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "run finish";
        })
        {
            @Override
            protected void done() {
                try {
                    //get the result
                    Object o = get();
                    System.out.println(o);
                    printIsMain();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    executorService.shutdownNow();
                }
            }
        };
        executorService.execute(futureTask);
    }


    private static void printIsMain() {
        System.out.println(Thread.currentThread().getId());
    }

    private static int getNum(int num) {
        while (num >= 10) {
            num = num / 10 + num % 10;//计算后赋值给num用于下次while循环
        }
        return num;
    }


    private static void runSemaphore() {
        final Semaphore semaphore = new Semaphore(1);

        Thread thread1 = new Thread(() -> {
            System.out.println("run in thread1");
            semaphore.release();
        });
        Thread thread2 = new Thread(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            try {
                semaphore.acquire();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("run in thread2");
            semaphore.release();

        });
        Thread thread3 = new Thread(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            try {
                semaphore.acquire();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("run in thread3");
            semaphore.release();
        });

        try {
            semaphore.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        thread1.start();
        thread2.start();
        thread3.start();
    }
}
