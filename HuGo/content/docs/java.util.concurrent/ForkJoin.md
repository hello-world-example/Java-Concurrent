# ForkJoin

ForkJoin 是一个 **基于分而治之算法**， 把大任务分割成若干个小任务，最终汇总每个小任务结果后，得到大任务结果的**框架**。框架基于以下两种操作：

- **`fork` 操作**：把任务分成更小的任务，使用这个框架执行它们
- **`join` 操作**：等待创建的任务的计算结束

![https://www.infoq.cn/article/fork-join-introduction](https://static001.infoq.cn/resource/image/2b/09/2be16e00a8ee6f6c7b738817f003e609.png)

## 与 Executor 的区别

- Executor 是所有工作线程公用一个任务队列，**所有的线程消费一个任务队列里面的任务**
- ForkJoin 是 每个线程都有一个任务对象，**每个线程消费各自任务队列的任务**

---

- 这样的话， ForkJoin 可能会导致一个问题，**假如有些任务队列早早被消费完了，则该队列对应的线程就闲置了**
-  `Executor` 则不会出现该问题，因为只有一个队列，当线程执行完一个任务后，重新从队列中获取任务，直到所有的任务执行换才会闲置

---

- ForkJoin 为了解决任务队列消费速度不一致导致的闲置问题，当一个任务队列执行完之后，就去其他线程的队列里**窃取**一个任务来执行，还给这个行为起了一个高大上的名字：**工作窃取（work-stealing）算法**。JDK 1.8 可以简单的通过 `ExecutorService executor = Executors.newWorkStealingPool(4)` 获取改类型的  `Executor` 
- 解决了线程闲置问题， `Executor` 和 `ForkJoin` 的线程都可达到满负荷运行了，这时候的区别在于**两者对数据的竞争状态不一样**
  - `Executor` 始终都是所有的线程竞争一个队列的数据
  -  `ForkJoin` 只有在 “窃取” 其他线程任务队里的最后一条数据的时候才产生竞争



## 核心类

- `ForkJoinPool`：它实现 ExecutorService 接口 和 work-stealing 算法，管理工作线程和提供关于任务的状态和执行的信息
- `ForkJoinTask`： 它是是在 ForkJoinPool 中执行的任务的基类，提供在任务中执行 `fork()` 和 `join()` 操作的机制。通常， 为了实现你的 Fork/Join 任务，需要实现 `ForkJoinTask` 的子类
  - `RecursiveAction` 处理**没有返回值**的任务
  - `RecursiveTask` 处理**有返回值**的任务

## 编程流程

一个使用 ForkJoin 框架累加的示例

```java
public class ForkJoinMain {

  @AllArgsConstructor
  static class Task extends RecursiveTask<Integer> {
    static final int TASK_SIZE = 2;

    List<Integer> data; 
    int start; 
    int end;

    /*
     * 需要自己实现拆分和计算逻辑
     */
    @Override
    protected Integer compute() {
      final int taskSize = end - start;
      /*
       * 2. 当任务已经拆分到了指定的阀值，直接结算结果
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
       * 1. 没有拆分到了指定的阀值，❤❤❤ 拆分任务 ❤❤❤
       */
      final int middle = (end + start) / 2;
      Task task1 = new Task(data, start, middle);
      Task task2 = new Task(data, middle, end);

      // task1.fork(); 
      // task2.fork();
      invokeAll(task1, task2);

      /*
       * 3. ❤❤ 汇总❤❤ 每个小任务的结果
       */
      return task1.join() + task2.join();
    }
  }

  public static void main(String[] args) throws InterruptedException, ExecutionException {
    // 数据
    final List<Integer> data = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
    // 任务
    final Task task = new Task(data, 0, data.size());

    // 提交任务 方式1
    Integer result = new ForkJoinPool(4).invoke(task);
    System.out.println(result);

    // 提交任务 方式2
    final Future<Integer> future = ForkJoinPool.commonPool().submit(task);
    System.out.println(future.get()); // 获取返回结果
  }
}
```

从以上示例可以看出来

- ForkJoin 只负责并行执行，**不负责如何 拆分和合并**
- 框架的计算逻辑需要自己实现，包括 **如何拆分任务** 和 **如何合并任务**
- 整个流程有点类似于 Map / Reduce 模式，**并行小批量计算 + 最终汇总**

## parallelism

一般构建 `ForkJoinPool`  有以下几种方式，这个重点关注并行数，目的是**关注在没有CPU限制的虚拟环境中运行时，出现的并行数过多问题**

### new ForkJoinPool(parallelism)

指定的多少就是多少

### new ForkJoinPool()

构造函数空参数，**大部分情况下，并行数就是 CPU 个数**

计算方式 `Math.min(MAX_CAP, Runtime.getRuntime().availableProcessors()`，`MAX_CAP` = `32767`

### ForkJoinPool.commonPool()

- ❤ 读取系统属性设置的 **`java.util.concurrent.ForkJoinPool.common.parallelism`** 值
- 如果没有，是 `availableProcessors - 1` 个

```java
private static ForkJoinPool makeCommonPool() {
  ...
    
  String pp = System.getProperty("java.util.concurrent.ForkJoinPool.common.parallelism");
  if (pp != null)
    parallelism = Integer.parseInt(pp);

  ...
  
  // default 1 less than #cores
  if (parallelism < 0 && (parallelism = Runtime.getRuntime().availableProcessors() - 1) <= 0)
    parallelism = 1;
  if (parallelism > MAX_CAP)
    parallelism = MAX_CAP;
  
  ...
}
```

## 常见的 ForkJoin 案例

### Arrays.parallelSort

```java
public static <T extends Comparable<? super T>> void parallelSort(T[] a) {
  int n = a.length, p, g;

  if (n <= MIN_ARRAY_SORT_GRAN || (p = ForkJoinPool.getCommonPoolParallelism()) == 1)
    // “数据个数 <= 8192” 或 “并行数只有 1” 的时候使用 TimSort 算法
    TimSort.sort(a, 0, n, NaturalOrder.INSTANCE, null, 0, 0);
  else
    // 否则使用 并行排序
    new ArraysParallelSortHelpers.FJObject.Sorter<T>(...).invoke();
}
```

### stream.parallel()

```java
final int sum = IntStream.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
        // 标记并行
        .parallel()
        // 在最终操作（Terminal）的时候选择 串行或并行 执行
        .sum();

final <R> R evaluate(TerminalOp<E_OUT, R> terminalOp) {
  ...
  return isParallel()
    ? terminalOp.evaluateParallel(this, sourceSpliterator(terminalOp.getOpFlags()))
    : terminalOp.evaluateSequential(this, sourceSpliterator(terminalOp.getOpFlags()));
}

@Override
public <P_IN> R evaluateSequential(PipelineHelper<T> helper, Spliterator<P_IN> spliterator){
  return helper.wrapAndCopyInto(makeSink(), spliterator).get();
}

@Override
public <P_IN> R evaluateParallel(PipelineHelper<T> helper, Spliterator<P_IN> spliterator) {
  // ❤❤❤ ReduceTask
  return new ReduceTask<>(this, helper, spliterator).invoke().get();
}
```



## Read More

- [聊聊并发（八）——Fork/Join 框架介绍](https://www.infoq.cn/article/fork-join-introduction)
- [RecursiveTask 和 RecursiveAction 的使用](https://blog.csdn.net/weixin_41404773/article/details/80733324)
- ifeve Fork/Join框架
  - [Fork/Join框架（一）引言](http://ifeve.com/fork-join-1/)
  - [Fork/Join框架（二）创建一个Fork/Join池](http://ifeve.com/fork-join-2/)
  - [Fork/Join框架（三）加入任务的结果](http://ifeve.com/fork-join-3/)
  - [Fork/Join框架（四）异步运行任务](http://ifeve.com/fork-join-4/)
  - [Fork/Join框架（五）在任务中抛出异常](http://ifeve.com/fork-join-5/)
  - [Fork/Join框架（六）取消任务](http://ifeve.com/fork-join-6/)