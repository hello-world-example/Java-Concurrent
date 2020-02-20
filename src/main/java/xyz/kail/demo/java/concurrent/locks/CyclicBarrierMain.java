package xyz.kail.demo.java.concurrent.locks;

import xyz.kail.demo.java.concurrent.utils.ThreadUtil;
import xyz.kail.demo.java.concurrent.utils.Tool;

import java.util.Random;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class CyclicBarrierMain {

    public static void main(String[] args) {
        CyclicBarrier cyclicBarrier = new CyclicBarrier(3);

        Runnable work = () -> {
            final int sleep = Math.abs(new Random().nextInt() % 10);
            Tool.println("sleep " + sleep);
            ThreadUtil.sleep(sleep, TimeUnit.SECONDS);
        };
        Consumer<CyclicBarrier> await = (CyclicBarrier barrier) -> {
            try {
                barrier.await();
            } catch (Exception e) {
                e.printStackTrace();
            }
        };

        final Runnable runnable = () -> {
            work.run();
            // 等待所有线程准备好
            await.accept(cyclicBarrier);
            Tool.println("---A---");


            work.run();
            // 等待所有线程准备好
            await.accept(cyclicBarrier);
            Tool.println("---B---");
        };

        new Thread(runnable).start();
        new Thread(runnable).start();
        new Thread(runnable).start();
    }

}
