package xyz.kail.demo.java.concurrent.complete;

import xyz.kail.demo.java.concurrent.utils.ThreadUtil;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class RequestParallelMain {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        CompletableFuture<Integer> future1 = CompletableFuture.supplyAsync(() -> {
            ThreadUtil.sleep(1, TimeUnit.SECONDS);
            return 1;
        });
        CompletableFuture<Integer> future2 = CompletableFuture.supplyAsync(() -> {
            ThreadUtil.sleep(2, TimeUnit.SECONDS);
            return 2;
        });
        CompletableFuture<Integer> future3 = CompletableFuture.supplyAsync(() -> {
            ThreadUtil.sleep(3, TimeUnit.SECONDS);
            return 3;
        });

        final long start = System.currentTimeMillis();
        System.out.println(thenCombine(future1, future2, future3));
        System.out.println(System.currentTimeMillis() - start);

    }

    static Integer thenCombine(CompletableFuture<Integer>... futures) throws ExecutionException, InterruptedException {
        CompletableFuture<Integer> root = CompletableFuture.completedFuture(0);
        for (CompletableFuture<Integer> future : futures) {
            root = root.thenCombine(future, Integer::sum);
        }
        return root.get();
    }
}
