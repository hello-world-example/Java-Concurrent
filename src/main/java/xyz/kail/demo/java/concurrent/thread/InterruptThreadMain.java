package xyz.kail.demo.java.concurrent.thread;

import java.util.concurrent.TimeUnit;

public class InterruptThreadMain implements Runnable {

    @Override
    public void run() {
        for (; ; ) {
            System.out.println(System.currentTimeMillis());

            System.out.println("thread.isInterrupted()" + Thread.currentThread().isInterrupted());
            System.out.println("thread.isInterrupted()" + Thread.currentThread().isInterrupted());
//            System.out.println("Thread.interrupted()" + Thread.interrupted());
//            System.out.println("Thread.interrupted()" + Thread.interrupted());

//            for (long i = 0; i < (Long.MAX_VALUE / 1_000_000_000); i++) {
//            }

            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    public static void main(String[] args) throws InterruptedException {
        final Thread thread = new Thread(new InterruptThreadMain());
        thread.start();

        TimeUnit.SECONDS.sleep(1);

        thread.interrupt();
    }
}
