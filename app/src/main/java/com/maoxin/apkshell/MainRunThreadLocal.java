package com.maoxin.apkshell;

/**
 * @author lmx
 * Created by lmx on 2019/12/27.
 *
 * Handler的Looper获取方式基于ThreadLocal
 * Android 高级面试-1：Handler 相关
 * <a href = "https://juejin.im/post/5c6a9a106fb9a04a0c2f0093"/>
 */
public class MainRunThreadLocal {

    /**
     * 由于每个线程维护的Hash表是独立的，因此在不同的Hash表中，key值即使相同也是没问题的，
     * 如果设置非静态，则在类中的每个ThreadLocal的实例中都会产生一个新对象，这是毫无意义的，只是增加了内存消耗。
     */
    private static ThreadLocal<Object> sThreadLocal = new ThreadLocal<>();

    public static void main(String[] args) {

        // testThreadLocalDefault();

        testThreadLocalSync();
    }

    private static void testThreadLocalDefault() {
        try {
            sThreadLocal.set("BBB");
            System.out.println("thread local:" + sThreadLocal.get());
            System.out.println("thread local:" + sThreadLocal.get());
        }
        finally {
            sThreadLocal.remove();
        }
    }

    private static void testThreadLocalSync() {
        ThreadLocal<Object> threadLocal = sThreadLocal;
        final int count = 5;
        Thread thread1;
        Thread thread2;
        thread1 = new Thread(() -> {
            try {
                for (int i = 0; i < count; i++) {
                    Thread.sleep(300);
                    threadLocal.set("thread-1-" + i);
                    System.out.println("thread1 ==> :" + threadLocal.get());
                }
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        thread2 = new Thread(() -> {
            try {
                for (int i = 0; i < count; i++) {
                    Thread.sleep(200);
                    threadLocal.set("thread-2-" + i);
                    System.out.println("thread2 ==> :" + threadLocal.get());
                }
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        thread1.start();
        thread2.start();
    }
}
