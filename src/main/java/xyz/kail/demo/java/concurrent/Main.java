package xyz.kail.demo.java.concurrent;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {

    public static void main(String[] args) throws InterruptedException {
        final Thread thread = new Thread(() -> {
            System.out.println(Thread.currentThread().isInterrupted());
            try {
                TimeUnit.SECONDS.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            System.out.println("继续执行" + Thread.currentThread().isInterrupted());

        });
        thread.start();

        System.out.println(thread.isInterrupted());

        thread.isAlive();


        TimeUnit.SECONDS.sleep(2);
        thread.interrupt();

    }

}
