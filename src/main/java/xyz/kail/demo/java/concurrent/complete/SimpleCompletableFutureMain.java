package xyz.kail.demo.java.concurrent.complete;

import java.util.concurrent.CompletableFuture;

public class SimpleCompletableFutureMain {

    private static void sleep(long timeout) {
        try {
            Thread.sleep(timeout);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    static Double fetchPrice() {
        sleep(1000);

        final double random = Math.random();

        if (random < 0.3) {
            throw new RuntimeException("fetch price failed!");
        }
        return random;
    }

    public static void main(String[] args) throws Exception {
        // 创建异步执行任务:
        CompletableFuture<Double> cf = CompletableFuture.supplyAsync(SimpleCompletableFutureMain::fetchPrice);
        // 如果执行成功:
        cf.thenAccept((result) -> System.out.println("price: " + result));
        // 如果执行异常:
        cf.exceptionally((e) -> {
            e.printStackTrace();
            return null;
        });

        final CompletableFuture<Integer> completedFuture = CompletableFuture.completedFuture(1);

        // 阻塞等待 thenAccept 回调
        cf.get();
    }


}
