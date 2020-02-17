package xyz.kail.demo.java.concurrent.complete;


import xyz.kail.demo.java.concurrent.utils.ThreadUtil;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;

public class Main {

    public static void main(String[] args) throws ExecutionException, InterruptedException {

        final CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
            ThreadUtil.sleep(1, TimeUnit.SECONDS);
            return 1 / 0;
        });

        final CompletableFuture<Integer> future2 = CompletableFuture.supplyAsync(() -> {
            ThreadUtil.sleep(2, TimeUnit.SECONDS);
            return 2;
        });



    }

}
