package xyz.kail.demo.java.concurrent.thread;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class AliveThreadMain implements Runnable {

    private static void sleep(long timeout, TimeUnit timeUnit) {
        try {
            timeUnit.sleep(timeout);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        for (int i = 0; i < 3; i++) {

            System.out.println(System.currentTimeMillis());
            // TIME_WAITING
            sleep(1, TimeUnit.SECONDS);
            // BLOCK
            lock(1L);
        }
    }

    /**
     * NEW:false
     * RUNNABLE:true
     * TIMED_WAITING::true
     * TERMINATED:false
     */
    public static void main(String[] args) {
        final Thread thread = new Thread(new AliveThreadMain());

        System.out.println(thread.getState() + ":" + thread.isAlive());
        thread.start();
        System.out.println(thread.getState() + ":" + thread.isAlive());

        new Thread(() -> {
            for (int i = 0; i < 6; i++) {
                sleep(500, TimeUnit.MILLISECONDS);
                System.out.println(thread.getState() + "::" + thread.isAlive());
            }
        }).start();

        System.out.println("lock(5_000L);");
        lock(5_000L);

        sleep(5, TimeUnit.SECONDS);
        System.out.println(thread.getState() + ":" + thread.isAlive());
    }

    private synchronized static void lock(long timeout) {
        System.out.println(Thread.currentThread().getName() + "-" + new Date());

        sleep(3, TimeUnit.SECONDS);

    }


}
