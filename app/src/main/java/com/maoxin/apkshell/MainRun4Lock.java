package com.maoxin.apkshell;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author lmx
 * Created by lmx on 2020/1/6.
 */
public class MainRun4Lock {
    private final Object object = new Object();
    private static int count = 0;

    public static void main(String[] args) {
        // testSpinningLock();

        // MainRun4Lock lock = new MainRun4Lock();
        // lock.testReEntryLock();

        SubClass subClass = new SubClass();
        subClass.doAAA();
    }

    private static class BaseClass {
        synchronized void doAAA() {
            System.out.println("doAAA");
        }
    }

    private static class SubClass extends BaseClass {
        @Override
        synchronized void doAAA() {
            super.doAAA();
            System.out.println("sub doAAA");
        }
    }


    /**
     * 可重入锁
     */
    private synchronized void testReEntryLock() {
        synchronized (object) {
            System.out.println("testReEntryLock");
            testReEntryLock2();
        }
    }

    private synchronized void testReEntryLock2() {
        synchronized (object) {
            System.out.println("testReEntryLock2");
        }
    }

    private static void testSpinningLock() {
        try {
            count = 0;
            ExecutorService executorService = Executors.newFixedThreadPool(100);
            CountDownLatch countDownLatch = new CountDownLatch(100);
            SpinningLock spinningLock = new SpinningLock();
            for (int i = 0; i < 100; i++) {
                executorService.execute(() -> {
                    spinningLock.lock();
                    ++count;
                    spinningLock.unlock();
                    countDownLatch.countDown();
                });
            }
            countDownLatch.await();
            System.out.println(count);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 自旋锁测试
     */
    private static class SpinningLock {
        AtomicReference<Thread> atomicReference = new AtomicReference<>();

        void lock() {
            Thread thread = Thread.currentThread();
            while (!atomicReference.compareAndSet(null, thread)) {
                System.out.println("doing lock");
            }
        }

        void unlock() {
            Thread thread = Thread.currentThread();
            if (!atomicReference.compareAndSet(thread, null)) {
                System.out.println("doing unlock");
            }
            atomicReference.set(null);
        }
    }
}
