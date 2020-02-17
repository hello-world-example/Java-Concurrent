package xyz.kail.demo.java.concurrent.forkjoin;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;

public class ForkJoinExMain extends ForkJoinTask {

    /**
     * @see ForkJoinPool#makeCommonPool
     * @see ForkJoinPool#externalPush <- submit / execute
     * @see ForkJoinPool#externalSubmit <- submit / execute
     */
    public static void main(String[] args) {
        final ExecutorService executor = ForkJoinPool.commonPool();

        final ForkJoinPool forkJoinPool = ForkJoinPool.commonPool();
        forkJoinPool.submit(()->{});
        // forkJoinPool.execute();

    }

    @Override
    public Object getRawResult() {
        return null;
    }

    @Override
    protected void setRawResult(Object value) {

    }

    @Override
    protected boolean exec() {
        return false;
    }
}
