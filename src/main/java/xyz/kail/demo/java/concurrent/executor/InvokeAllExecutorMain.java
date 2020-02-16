package xyz.kail.demo.java.concurrent.executor;

import lombok.SneakyThrows;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class InvokeAllExecutorMain {

    public static void main(String[] args) throws InterruptedException {
        final ExecutorService threadPool = Executors.newCachedThreadPool();

        List<Callable<String>> tasks = Arrays.asList(
                () -> sleep(4),
                () -> sleep(1),
                () -> sleep(3)
        );

        long start = System.currentTimeMillis();
        final List<Future<String>> futures = threadPool.invokeAll(tasks);
        System.out.println("执行耗时 " + (System.currentTimeMillis() - start));

    start = System.currentTimeMillis();
    Map<String, String> result = new HashMap<>();
    result.put("a", get(futures.get(0)));
    result.put("b", get(futures.get(1)));
    result.put("c", get(futures.get(2)));
    System.out.println(result);
    System.out.println("获取数据耗时 " + (System.currentTimeMillis() - start));

        threadPool.shutdown();
    }

    private static <V> V get(Future<V> future) {
        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }


    @SneakyThrows
    private static String sleep(int sleep) {
        if (sleep == 0) {
            throw new IllegalArgumentException("" + sleep);
        }

        TimeUnit.SECONDS.sleep(sleep);
        System.out.println(Thread.currentThread().getName() + " 耗时 " + sleep);
        return String.valueOf(sleep);
    }


}
