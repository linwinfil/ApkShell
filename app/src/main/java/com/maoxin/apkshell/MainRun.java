package com.maoxin.apkshell;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

/**
 * @author lmx
 * Created by lmx on 2020/2/25.
 */
public class MainRun {

    public static void main(String[] args) {
        // System.out.println(getNum(100));


        runFutureTask();
        String[] a = new String[10];
        for (String s : a) {

        }
    }


    private static void runFutureTask() {
        ExecutorService executorService = Executors.newCachedThreadPool();

        FutureTask<Object> futureTask = new FutureTask<Object>(() -> {
            //doing something
            printIsMain();
            try {
                Thread.sleep(5_000);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "run finish";
        }) {
            @Override
            protected void done() {
                try {
                    //get the result
                    Object o = get();
                    System.out.println(o);
                    printIsMain();
                }
                catch (Exception e) {
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
}
