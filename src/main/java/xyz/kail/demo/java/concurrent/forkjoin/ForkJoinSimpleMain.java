package xyz.kail.demo.java.concurrent.forkjoin;

import lombok.AllArgsConstructor;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;

public class ForkJoinSimpleMain {


    @AllArgsConstructor
    static class Task extends RecursiveTask<Integer> {

        static final int TASK_SIZE = 2;

        List<Integer> data;
        int start;
        int end;

        @Override
        protected Integer compute() {
            final int taskSize = end - start;
            /*
             * 当任务已经拆分到了指定的阀值，直接结算结果
             */
            if (taskSize <= TASK_SIZE) {
                int sum = 0;
                for (int i = start; i < end; i++) {
                    sum += data.get(i);
                }
                // 小批任务的计算结果
                return sum;
            }

            /*
             * 没有拆分到了指定的阀值，拆分任务
             */
            final int middle = (end + start) / 2;
            Task task1 = new Task(data, start, middle);
            Task task2 = new Task(data, middle, end);
            task1.fork();
            task2.fork();

            /*
             * 汇总每个小任务的结果
             */
            return task1.join() + task2.join();
        }
    }


    public static void main(String[] args) throws InterruptedException, ExecutionException {
        final List<Integer> data = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        final Task task = new Task(data, 0, data.size());

        Integer result = new ForkJoinPool(4).invoke(task);
        System.out.println(result);

        final Future<Integer> future = ForkJoinPool.commonPool().submit(task);
        System.out.println(future.get());
    }

}
