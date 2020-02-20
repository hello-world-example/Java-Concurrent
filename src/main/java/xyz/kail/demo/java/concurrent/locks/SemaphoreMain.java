package xyz.kail.demo.java.concurrent.locks;

import xyz.kail.demo.java.concurrent.utils.ThreadUtil;
import xyz.kail.demo.java.concurrent.utils.Tool;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class SemaphoreMain {

    public static void main(String[] args) throws InterruptedException {
        int permits = 10;

        Semaphore semaphore = new Semaphore(0);

        new Thread(() -> {
            for (int i = 0; i < permits; i++) {
                ThreadUtil.sleep(1, TimeUnit.SECONDS);
                // 偶数释放 1个，奇数释放 2个
                semaphore.release(i % 2 + 1);
            }
        }).start();

        for (int i = 0; i < permits; i++) {
            semaphore.acquire();
            // 获取许可后执行
            new Thread(() -> Tool.println(Thread.currentThread().getName() + "通过")).start();
        }

    }

}
