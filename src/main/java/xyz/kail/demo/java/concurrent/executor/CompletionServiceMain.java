package xyz.kail.demo.java.concurrent.executor;

import xyz.kail.demo.java.concurrent.utils.ThreadUtil;

import java.util.concurrent.*;

public class CompletionServiceMain {

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        ExecutorService threadPool = Executors.newCachedThreadPool();
        CompletionService<Long> completionService = new ExecutorCompletionService<>(threadPool);

        completionService.submit(() -> ThreadUtil.sleep(1, TimeUnit.SECONDS), 1L);
        completionService.submit(() -> ThreadUtil.sleep(2, TimeUnit.SECONDS), 2L);
        completionService.submit(() -> ThreadUtil.sleep(3, TimeUnit.SECONDS), 3L);

        for (; ; ) {
            System.out.println(completionService.take().get());
        }
    }

}
