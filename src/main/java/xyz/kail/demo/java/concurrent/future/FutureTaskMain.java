package xyz.kail.demo.java.concurrent.future;

import java.util.concurrent.*;

public class FutureTaskMain {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        final ExecutorService threadExecutor = Executors.newSingleThreadExecutor();
        final Future<String> future = threadExecutor.submit(() -> {
            return "ok";
        });

        FutureTask<String> futureTask = new FutureTask<>(() -> {
            return "1";
        });

        futureTask.run();
        futureTask.get();

    }

}
