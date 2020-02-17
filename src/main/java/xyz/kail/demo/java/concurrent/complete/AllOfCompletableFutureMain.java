package xyz.kail.demo.java.concurrent.complete;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class AllOfCompletableFutureMain {

    public static void main(String[] args) throws ExecutionException, InterruptedException {

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
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return 1;
        });

        long startTime = System.currentTimeMillis();

        final CompletableFuture<Void> voidCompletableFuture = CompletableFuture.allOf(oddNumber, evenNumber, testNumber);
        System.out.println(voidCompletableFuture.get());

        System.out.println("oddNumber " + oddNumber.get());
        System.out.println("evenNumber " + evenNumber.get());
        System.out.println("testNumber " + testNumber.get());

        System.out.println("0.开始了：" + (System.currentTimeMillis() - startTime) + "秒");
    }

}
