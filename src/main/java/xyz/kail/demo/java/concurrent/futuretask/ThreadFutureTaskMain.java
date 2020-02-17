package xyz.kail.demo.java.concurrent.futuretask;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

public class ThreadFutureTaskMain {

    public static void main(String[] args) throws ExecutionException, InterruptedException {

        FutureTask<String> futureTask = new FutureTask<>(() -> {
            Thread.sleep(3000);
            return "ok";
        });

        // 开启线程执行
        new Thread(futureTask).start();

        System.out.println("waiting." + System.currentTimeMillis());
        final String result = futureTask.get(); // 等待执行结果
        System.out.println(result + "......" + System.currentTimeMillis());

    }

}
