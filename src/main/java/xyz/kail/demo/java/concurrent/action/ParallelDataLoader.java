package xyz.kail.demo.java.concurrent.action;

import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ParallelDataLoader extends DataLoader {

    protected void doLoad() {  // 并行计算

        ExecutorService executorService = Executors.newFixedThreadPool(3); // 创建线程池
        CompletionService<Object> completionService = new ExecutorCompletionService<>(executorService);

        completionService.submit(super::loadConfigurations, null);      //  耗时 >= 1s
        completionService.submit(super::loadUsers, null);               //  耗时 >= 2s
        completionService.submit(super::loadOrders, null);              //  耗时 >= 3s

        int count = 0;
        // 等待三个任务完成
        while (count < 3) {
            if (completionService.poll() != null) {
                count++;
            }
        }
        executorService.shutdown();

        // 总耗时 max(1s, 2s, 3s)  >= 3s
    }

    /**
     * [线程 : pool-1-thread-1] loadConfigurations() 耗时 :  1000 毫秒
     * [线程 : pool-1-thread-2] loadUsers() 耗时 :  2002 毫秒
     * [线程 : pool-1-thread-3] loadOrders() 耗时 :  3005 毫秒
     * load() 总耗时：3200 毫秒
     */
    public static void main(String[] args) {
        new ParallelDataLoader().load();
    }

}
