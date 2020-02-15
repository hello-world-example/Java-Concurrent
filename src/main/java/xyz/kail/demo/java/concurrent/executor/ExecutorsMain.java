package xyz.kail.demo.java.concurrent.executor;

import lombok.SneakyThrows;

import java.util.Date;
import java.util.concurrent.*;

public class ExecutorsMain implements Runnable {

    public static void main(String[] args) throws InterruptedException {
        final BlockingQueue<Runnable> queue = new LinkedBlockingQueue<>(3);

        final ExecutorService executor = Executors.newWorkStealingPool(2);
        executor.execute(new ExecutorsMain(2));
        executor.execute(new ExecutorsMain(2));
        executor.execute(new ExecutorsMain(2));
        executor.execute(new ExecutorsMain(2));


        System.out.println("join");
    }

    int sleep;

    public ExecutorsMain(int sleep) {
        this.sleep = sleep;
    }

    @SneakyThrows
    @Override
    public void run() {
        System.out.println(sleep);
        TimeUnit.SECONDS.sleep(sleep);
    }
}
