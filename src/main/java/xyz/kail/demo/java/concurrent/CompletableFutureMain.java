package xyz.kail.demo.java.concurrent;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class CompletableFutureMain {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
//        final CompletableFuture<Object> future = new CompletableFuture<>();
//
//        final ExecutorService executorService = Executors.newCachedThreadPool();
//        final Future<?> submit = executorService.submit(() -> {
//        });
        CompletableFuture<Integer> oddNumber = CompletableFuture.supplyAsync(() -> {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return 1;
        });
        CompletableFuture<Integer> evenNumber = CompletableFuture.supplyAsync(() -> {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return 1;
        });
        CompletableFuture<Integer> testNumber = CompletableFuture.supplyAsync(() -> {
            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return 1;
        },null);

        long startTime = System.currentTimeMillis();

        final CompletableFuture<Void> voidCompletableFuture = CompletableFuture.allOf(oddNumber, evenNumber, testNumber);
        System.out.println(voidCompletableFuture.get());

        System.out.println("0.开始了：" + (System.currentTimeMillis() - startTime) + "秒");
    }

}
