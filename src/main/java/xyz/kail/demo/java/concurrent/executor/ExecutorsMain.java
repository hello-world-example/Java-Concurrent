package xyz.kail.demo.java.concurrent.executor;

import lombok.SneakyThrows;

import java.util.concurrent.TimeUnit;

public class ExecutorsMain {

    public static void main(String[] args) throws InterruptedException {
        final Thread thread = new Thread(() -> {
            System.out.println(Thread.State.RUNNABLE + ":" + Thread.currentThread().getState());
            sleep(1);

            lock(1);
        });


        System.out.println(Thread.State.NEW + ":" + thread.getState());
        thread.start();

        new Thread(() -> {
            lock(3_000);
            System.out.println(thread.getState());
        }).start();

        thread.join();
        System.out.println(Thread.State.TERMINATED + ":" + thread.getState());
    }

    @SneakyThrows
    private synchronized static void lock(int sleep) {
        ExecutorsMain.class.wait(sleep);

        ExecutorsMain.class.notifyAll();
    }

    @SneakyThrows
    private static void sleep(int sleep) {
        TimeUnit.SECONDS.sleep(sleep);
    }


}
