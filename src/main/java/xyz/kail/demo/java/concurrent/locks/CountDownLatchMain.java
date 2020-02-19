package xyz.kail.demo.java.concurrent.locks;

import xyz.kail.demo.java.concurrent.utils.ThreadUtil;
import xyz.kail.demo.java.concurrent.utils.Tool;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class CountDownLatchMain {

    static void waitChildren() throws InterruptedException {
        int count = 3;

        CountDownLatch countDownLatch = new CountDownLatch(count);

        for (int i = 0; i < 3; i++) {
            int finalI = i;
            new Thread(() -> {
                ThreadUtil.sleep(finalI, TimeUnit.SECONDS);

                Tool.println(Thread.currentThread().getName() + " 完成");
                // count - 1
                countDownLatch.countDown();
            }).start();
        }

        Tool.println("等待 " + count + "个 任务完成");
        // 阻塞当前线程
        countDownLatch.await();
        Tool.println(count + "个 任务都已完成!! ");
    }

    static void waitMain() throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(1);

        for (int i = 0; i < 3; i++) {
            new Thread(() -> {
                Tool.println(Thread.currentThread().getName() + " 等待主线程");

                try {
                    countDownLatch.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                Tool.println(Thread.currentThread().getName() + " 开始");
            }).start();
        }

        ThreadUtil.sleep(3, TimeUnit.SECONDS);
        Tool.println("开始干活");
        countDownLatch.countDown();
    }

    public static void main(String[] args) throws InterruptedException {
        waitMain();
    }

}
