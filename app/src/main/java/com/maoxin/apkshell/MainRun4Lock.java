package com.maoxin.apkshell;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
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

        // SubClass subClass = new SubClass();
        // subClass.doAAA();

        // testSemaphore();

        // testCountDownLatch();

        try
        {
            testCyclicBarrier();
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
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

    /** 信号量 访问 */
    private static final void testSemaphore() {
        // Semaphore semaphore = new Semaphore(20); //抢占式
        Semaphore semaphore = new Semaphore(5, true);//公平

        ExecutorService executorService = Executors.newFixedThreadPool(100);

        for (int i = 0; i < 50; i++)
        {
            int finalI = i;
            executorService.execute(() -> {
                try {
                    System.out.println("--- start ---");
                    semaphore.acquire();
                    test(finalI);
                }  catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    semaphore.release();
                    System.out.println("--- end ---");
                }
            });
        }
        executorService.shutdown();
        System.out.println("finish");
    }

    /** 倒数计数器 */
    private static final void testCountDownLatch() {
        CountDownLatch countDownLatch = new CountDownLatch(100);
        ExecutorService executorService = Executors.newFixedThreadPool(50);

        for (int i = 0; i < 100; i++)
        {
            int finalI = i;
            executorService.execute(() -> {
                try
                {
                    test(finalI);
                }
                catch (Exception t)
                {
                    t.printStackTrace();
                }
                finally
                {
                    countDownLatch.countDown();
                }
            });
        }
        try
        {
            countDownLatch.await();
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        executorService.shutdown();
        System.out.println("finish");

    }

    private static final void testCyclicBarrier() throws InterruptedException
    {
        CyclicBarrier cyclicBarrier = new CyclicBarrier(5, () -> System.out.println("barrierAction"));
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        for (int i = 0; i < 100; i++)
        {
            Thread.sleep(1000);
            int finalI = i;
            executorService.execute(() -> {
                try
                {
                    test(cyclicBarrier, finalI);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            });
        }

        executorService.shutdown();

    }

    public static void test(CyclicBarrier cyclicBarrier, int threadnum) throws InterruptedException, BrokenBarrierException
    {
        System.out.println("threadnum:" + threadnum + "is ready");
        try {
            /** 等待30秒，保证子线程完全执行结束 */
            cyclicBarrier.await(30, TimeUnit.SECONDS);
        } catch (Exception e) {
            System.out.println("-----CyclicBarrierException------");
        }
        System.out.println("threadnum:" + threadnum + "is finish");
    }


    public static void test(int threadnum) throws InterruptedException {
        Thread.sleep(1000);// 模拟请求的耗时操作
        System.out.println("threadnum:" + threadnum);
        Thread.sleep(1000);// 模拟请求的耗时操作
    }


}
