# ExecutorService

> - [java.util.concurrent.ExecutorService](https://tool.oschina.net/uploads/apidocs/jdk-zh/java/util/concurrent/ExecutorService.html)
> - [java.util.concurrent.ThreadPoolExecutor](https://tool.oschina.net/uploads/apidocs/jdk-zh/java/util/concurrent/ThreadPoolExecutor.html)

## 继承关系

```
- Executor (java.util.concurrent)
  - ❤5❤ ExecutorService (java.util.concurrent)
    - AbstractExecutorService (java.util.concurrent)
      - ❤5❤ ThreadPoolExecutor (java.util.concurrent)
        - ❤5❤ ScheduledThreadPoolExecutor (java.util.concurrent)
      - ❤7❤ ForkJoinPool (java.util.concurrent)
    - ScheduledExecutorService (java.util.concurrent)
      - ScheduledThreadPoolExecutor (java.util.concurrent)
```

## ThreadPoolExecutor

ThreadPoolExecutor 的构造参数如下

```java
/**
* @param corePoolSize 最小线程数
* @param maximumPoolSize 最大线程数
* @param keepAliveTime corePoolSize 到 maximumPoolSize 线程的空闲时间，超过该时间会被释放
* @param unit 参数 keepAliveTime 的单位
* @param workQueue 在任务执行之前保存进该队列
* @param threadFactory 线程工厂，主要给线程器名字，❤ 默认：Executors.defaultThreadFactory()
* @param handler 任务数大于 maxPoolSize 和 队列大小时 的拒绝策略，❤ 默认：AbortPolicy，抛出拒绝异常
*/
public ThreadPoolExecutor(
    int corePoolSize,
    int maximumPoolSize,
    long keepAliveTime,
    TimeUnit unit,
    BlockingQueue<Runnable> workQueue,
    ThreadFactory threadFactory,
    RejectedExecutionHandler handler
){ ... }
```

### execute 与 submit 的区别

` ThreadPoolExecutor# void execute` 和 `AbstractExecutorService# Future<T> submit` 的区别就像方法签名本身一样，只是返回值不同，`submit` 内部还是调用的 `execute` 方法，只是把参数封装成 `FutureTask` 

```java
// @see AbstractExecutorService

public Future<?> submit(Runnable runnable) {
    if (task == null) throw new NullPointerException();
    // new FutureTask<T>(runnable, value)
    RunnableFuture<Void> ftask = newTaskFor(runnable, null);
    execute(ftask); // ❤
    return ftask;
}
```

> @see [Future]({{< relref "/docs/java.util.concurrent/Future.md" >}}) Page

execute 的执行要复杂许多，主要有以下几个步骤，详见 `ThreadPoolExecutor#execute` 代码注释

- 如果线程池中的线程数量少于 **`corePoolSize`**，就创建新的线程来执行新添加的任务
- 如果线程池中的线程数量大于等于 corePoolSize，但队列 **`workQueue`** 未满，则将新添加的任务放到workQueue 中
- 如果队列workQueue已满，但线程池中的线程数量小于**`maximumPoolSize`**，则会创建新的线程来处理被添加的任务
- 如果线程池中的线程数量等于了 maximumPoolSize ，就用 **`RejectedExecutionHandler`** 来执行拒绝策略

> 所以，假如一个任务执行需要 1分钟，为了避免触发拒绝策略，则一分钟内提交的任务不能超过多少？
>
> 答案是  **`workQueue.size()`**  + **`maxPoolSize`**，即 **队列中等待执行的任务** + **正在执行的任务**



### RejectedExecutionHandler

```java
// AbortPolicy (默认)： 抛出 RejectedExecutionException 异常
public static class AbortPolicy implements RejectedExecutionHandler {
  public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
    throw new RejectedExecutionException("Task " + r.toString() + 
                                         " rejected from " + e.toString());
  }
}

// CallerRunsPolicy： 调用线程的 run() 方法，即同步执行，能够减缓新任务的提交速度
public static class CallerRunsPolicy implements RejectedExecutionHandler {
  public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
    if (!e.isShutdown()) {
      r.run();
    }
  }
}

// DiscardPolicy： 丢弃当前
public static class DiscardPolicy implements RejectedExecutionHandler {
  public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
  }
}

// DiscardOldestPolicy： 丢弃最老的线程
public static class DiscardOldestPolicy implements RejectedExecutionHandler {
  public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
    if (!e.isShutdown()) {
      e.getQueue().poll();
      e.execute(r);
    }
  }
}
```



## 通过 Executors 工厂创建

### newSingleThreadExecutor

单例线程，只有一个线程的线程池

多余的任务都会放在 `LinkedBlockingQueue` 队列中，默认大小是 `Integer.MAX_VALUE`，**可能会内存溢出**

```java
new ThreadPoolExecutor(
  1,1, 
  0L,TimeUnit.MILLISECONDS, 
  new LinkedBlockingQueue<Runnable>())
)
```

### newFixedThreadPool(nThreads)

与 `newSingleThreadExecutor` 的 **区别仅仅在于线程池的大小**

```java
new ThreadPoolExecutor(
  nThreads, nThreads,
  0L, TimeUnit.MILLISECONDS,
  new LinkedBlockingQueue<Runnable>()
)
```

### newCachedThreadPool

`SynchronousQueue` 是一个**没有容量的队列**，插入(`put`)操作必须等待另一个线程的对应移除(`take`)操作，**如果没有消费线程，写入操作会一直阻塞**。

该操作创建的线程池，**所有丢进去的任务都会直接创建成工作线程**，因为 corePoolSize 是 0，**线程总数几乎没有限制**，所有创建的线程都受到 `keepAliveTime` 的管理，即 **1分钟空闲被回收**，通常用于执行一些生命周期很短的任务，快速创建又快速消逝。

```java
new ThreadPoolExecutor(
  0, Integer.MAX_VALUE,
  60L, TimeUnit.SECONDS,
  new SynchronousQueue<Runnable>()
)
```

### newScheduledThreadPool

调度型线程池，这个池子里的线程可以按 schedule **延迟执行** 或 **周期执行**。

```java
new ScheduledThreadPoolExecutor(corePoolSize);

public ScheduledThreadPoolExecutor(int corePoolSize) {
  super(
    corePoolSize, Integer.MAX_VALUE, 
    0, NANOSECONDS, 
    new DelayedWorkQueue()
  );
}
```

#### newSingleThreadScheduledExecutor

```java
new ScheduledThreadPoolExecutor(1)
```

### [1.8]newWorkStealingPool(parallelism)

> @see [1.7] [ForkJoinPool]({{< relref "/docs/java.util.concurrent/ForkJoinPool.md" >}}) Page

创建 parallelism 个线程 的线程池，来维持相应的并行级别，它会通过**工作窃取**的方式，使得多核的 CPU 不会闲置，总会有活着的线程让 CPU 去运行。**所谓工作窃取，指的是闲置的线程去处理本不属于它的任务**。每个处理器核，都有一个队列存储着需要完成的任务。对于多核的机器来说，当一个核对应的任务处理完毕后，就可以去帮助其他的核处理任务。

```java
new ForkJoinPool(
  parallelism,
  ForkJoinPool.defaultForkJoinWorkerThreadFactory,
  null, 
  true
)
```



## Read More

- [Java线程池ThreadPoolExecutor使用和分析(二) - execute()原理](https://www.cnblogs.com/trust-freedom/p/6681948.html)
- [Java 8 对线程池有哪些改进？](https://cloud.tencent.com/developer/article/1362826)