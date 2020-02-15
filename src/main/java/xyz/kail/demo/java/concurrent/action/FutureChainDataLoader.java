package xyz.kail.demo.java.concurrent.action;

import java.util.concurrent.CompletableFuture;

public class FutureChainDataLoader extends DataLoader {

    protected void doLoad() {
        CompletableFuture
                .runAsync(super::loadConfigurations)
                .thenRun(super::loadUsers)
                .thenRun(super::loadOrders)
                .whenComplete((result, throwable) -> { // 完成时回调
                    System.out.println("加载完成");
                })
                .join(); // 等待完成
    }

    /**
     * [线程 : ForkJoinPool.commonPool-worker-1] loadConfigurations() 耗时 :  1003 毫秒
     * [线程 : ForkJoinPool.commonPool-worker-1] loadUsers() 耗时 :  2001 毫秒
     * [线程 : ForkJoinPool.commonPool-worker-1] loadOrders() 耗时 :  3004 毫秒
     * 加载完成
     * load() 总耗时：6139 毫秒
     */
    public static void main(String[] args) {
        new FutureChainDataLoader().load();
    }
}

