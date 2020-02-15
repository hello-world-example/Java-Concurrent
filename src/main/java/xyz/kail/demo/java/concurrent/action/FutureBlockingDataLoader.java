package xyz.kail.demo.java.concurrent.action;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class FutureBlockingDataLoader extends DataLoader {

    protected void doLoad() {
        ExecutorService executorService = Executors.newFixedThreadPool(3); // 创建线程池
        runCompletely(executorService.submit(super::loadConfigurations));  //  耗时 >= 1s
        runCompletely(executorService.submit(super::loadUsers));           //  耗时 >= 2s
        runCompletely(executorService.submit(super::loadOrders));          //  耗时 >= 3s
        executorService.shutdown();
    } // 总耗时 sum(>= 1s, >= 2s, >= 3s)  >= 6s

    private void runCompletely(Future<?> future) {
        try {
            future.get(); // 阻塞等待结果执行
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * [线程 : pool-1-thread-1] loadConfigurations() 耗时 :  1002 毫秒
     * [线程 : pool-1-thread-2] loadUsers() 耗时 :  2003 毫秒
     * [线程 : pool-1-thread-3] loadOrders() 耗时 :  3002 毫秒
     * load() 总耗时：6167 毫秒
     *
     * @param args
     */
    public static void main(String[] args) {
        new FutureBlockingDataLoader().load();
    }

}
