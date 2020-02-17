package xyz.kail.demo.java.concurrent.forkjoin;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.*;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Main {


    public static void main(String[] args) throws Exception {
        final ForkJoinPool forkJoinPool = ForkJoinPool.commonPool();
        System.out.println(forkJoinPool.getPoolSize());
//        final int commonPoolParallelism = ForkJoinPool.getCommonPoolParallelism();
//
//        // 32767
        final ForkJoinPool forkJoinPool1 = new ForkJoinPool();
        System.out.println(forkJoinPool1.getPoolSize());
//        new ForkJoinPool(1);
//
//        System.out.println(0x7fff);

//        System.out.println(Runtime.getRuntime().availableProcessors());
//        System.out.println(ForkJoinPool.getCommonPoolParallelism());

        final Integer[] data = {1, 3, 2, 7, 4, 6, 9};
        Arrays.parallelSort(data);
        System.out.println(Arrays.asList(data));

        System.out.println(1 << 13);

        final IntStream intStream = IntStream.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
final int sum = IntStream.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
        // 标记并行
        .parallel()
        // 在最终操作的时候选择 串行或并行 执行
        .sum();
        System.out.println(sum);

        final ExecutorService executor = Executors.newWorkStealingPool();

    }


}

