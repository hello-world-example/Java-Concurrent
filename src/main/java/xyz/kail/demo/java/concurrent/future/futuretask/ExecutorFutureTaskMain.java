package xyz.kail.demo.java.concurrent.future.futuretask;

import java.util.List;
import java.util.concurrent.*;

public class ExecutorFutureTaskMain {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        final ExecutorService threadExecutor = Executors.newSingleThreadExecutor();

        FutureTask<String> futureTask = new FutureTask<>(() -> {
            Thread.sleep(3000);
            return "ok";
        });

        // 提交任务
        threadExecutor.execute(futureTask);

        System.out.println("waiting." + System.currentTimeMillis());
        final String result = futureTask.get(); // 等待执行结果
        System.out.println(result + "......" + System.currentTimeMillis());

        threadExecutor.shutdown();

    }

}
