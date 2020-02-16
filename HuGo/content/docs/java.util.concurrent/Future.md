# Future

> en [java.util.concurrent.Future](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/Future.html)
>
> zh [java.util.concurrent.Future](https://tool.oschina.net/uploads/apidocs/jdk-zh/java/util/concurrent/Future.html)

## Future 接口

Future 接口是**对异步行为的抽象**，用来**表示异步计算的结果**，接口比较简单，如下：

```java
public interface Future<V> {
  // 等待任务结束，获取结果
  V get() throws InterruptedException, ExecutionException;
  // 获取结果，设置超时时间
  V get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException;
  // 取消任务的执行
  boolean cancel(boolean mayInterruptIfRunning);
  // 正常完成前取消，返回 true
  boolean isCancelled();
  // 任务正常终止、异常或取消，返回true
  boolean isDone();
}
```

## 继承关系

- **Future** (java.util.concurrent)
	- RunnableFuture (java.util.concurrent) **extends Runnable**
		- **FutureTask** (java.util.concurrent)
	- **ForkJoinTask** (java.util.concurrent)
	- **CompletableFuture** (java.util.concurrent)
	- ...

## FutureTask 简介

FutureTask 是 Future 接口的基本实现，包装了 `Callable` 和 `Runnable` (内部转换成  Callable ) 接口对象。

- 当任务计算完成时，可通过 `get` 直接获取结果
- 当计算没有完成时，`get` 方法会一直阻塞直到任务转入完成状态
- 一旦完成计算，不能够重新开始或取消计算

一般通过 `Executor` 线程池 来执行，也可传递给 `Thread` 对象执行。如果在主线程中需要执行比较耗时的操作时，但又不想阻塞主线程时，可以把这些作业交给 `FutureTask` 在后台完成，当主线程将来需要时，就可以通过`Future` 对象获得后台作业的计算结果或者执行状态。

### 通过 Executor 执行

把 `threadExecutor.submit` 代码拆开，即是一下代码

```java
final ExecutorService threadExecutor = Executors.newSingleThreadExecutor();

FutureTask<String> futureTask = new FutureTask<>(() -> {
    Thread.sleep(3000); // 任务执行需要 3秒
    return "ok";
});

// 提交任务
threadExecutor.execute(futureTask);

System.out.println("waiting." + System.currentTimeMillis());
final String result = futureTask.get(); // 阻塞 等待执行结果
System.out.println(result + "......" + System.currentTimeMillis());

threadExecutor.shutdown();

// 输出
// waiting.15818730 27 104
// ok......15818730 30 105
```

### 通过 Thread 执行

```java
FutureTask<String> futureTask = new FutureTask<>(() -> {
    Thread.sleep(3000); // 任务执行需要 3秒
    return "ok";
});

// 开启线程执行
new Thread(futureTask).start();
// 这样也行，相当于 new Runnable(){ futureTask.run(); }
// new Thread(futureTask::run).start();

System.out.println("waiting." + System.currentTimeMillis());
final String result = futureTask.get(); // 等待执行结果
System.out.println(result + "......" + System.currentTimeMillis());

// 输出
// waiting.15818733 25 457
// ok......15818733 28 459
```

## FutureTask 原理

### 内部状态

|                |                                                              |      |
| -------------- | ------------------------------------------------------------ | ---- |
| `NEW`          | **初始状态**，`new FutureTask` 的时候设置                    |      |
| `COMPLETING`   | 标示任务执行完成，但是暂未保存结果的瞬时状态，<br />**正常结束 或 异常结束** 时先变为 `COMPLETING`，再获取结果内部保存，最后 设置为最终状态 |      |
| `NORMAL`       | **正常结束** 的 最终状态                                     |      |
| `EXCEPTIONAL`  | **异常结束** 的 最终状态                                     |      |
|                |                                                              |      |
| `CANCELLED`    | **任务取消**，`cancel(false)`                                |      |
| `INTERRUPTING` | **任务中断取消**的**中间状态**，调用 `thread.interrupt()` 置为 `INTERRUPTED` |      |
| `INTERRUPTED`  | **任务中断取消**的**最终状态**                               |      |

### 可能的状态转换

- `NEW -> COMPLETING -> NORMAL` ：正常结束
- `NEW -> COMPLETING -> EXCEPTIONAL` ：异常结束
- `NEW -> CANCELLED` ：任务取消，`cancel(false)`
- `NEW -> INTERRUPTING -> INTERRUPTED`：任务 `thread.interrupt()` 后取消，`cancel(true)`

### 状态的维护

- 主要通过 `run` / `cancel` 方法对内部状态的变更进行维护
- `run` 方法 是一个同步方法，且 同步调用 `Callable` 任务的 `call` 方法获取结果，**整个流程都是同步的**
- 异步执行的效果主要通过 Thread 线程 实现，**相对主线程是异步的，内部仍然是同步**
- 调用 `get` 方法的时候，内部会通过状态判断是否阻塞等待（通过 `LockSupport.park` 阻塞）
-  `run` / `cancel`  最后会通过 `LockSupport.unpark` 解除线程阻塞

> `LockSupport.park` / `LockSupport.unpark` 原理详见 [LockSupport](../locks/LockSupport/)

## ❤ 异步调用的示例

```java
@SneakyThrows
private static String sleep(int sleep) {
    if (sleep == 0) { throw new IllegalArgumentException("" + sleep); }
    TimeUnit.SECONDS.sleep(sleep);
    System.out.println(Thread.currentThread().getName() + " 耗时 " + sleep);
    return String.valueOf(sleep);
}

public static void main(String[] args) throws InterruptedException {
    final ExecutorService threadPool = Executors.newCachedThreadPool();

    List<Callable<String>> tasks = Arrays.asList(
            () -> sleep(4),
            () -> sleep(0), // 任务 2 会抛异常
            () -> sleep(3)
    );

    final long start = System.currentTimeMillis();
    final List<Future<String>> futures = threadPool.invokeAll(tasks); // invokeAll 同步执行
    System.out.println("总耗时 " + (System.currentTimeMillis() - start));
  
    // 三个任务并发执行的结果，封装成新的对象，一旦任务执行完成，获取结果非常快
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
```

>pool-1-thread-3 耗时 3  
>
>pool-1-thread-1 耗时 4   
>
>java.util.concurrent.ExecutionException: java.lang.IllegalArgumentException: 0 ........  
>
>总耗时 4009  
>
>{a=4, b=null, c=3}   
>
>获取数据耗时 0

## Future 的不足

虽然 JDK5 新增的 `Future` 用于描述一个异步计算的结果，但是对于结果的获取却是很不方便，只能通过 **阻塞** 或者 **轮询** 的方式得到任务的结果。

- **阻塞的方式显然和我们的异步编程的初衷相违背**
- **轮询的方式又会耗费无谓的 CPU 资源，而且也不能及时地得到计算结果**

## 改进方案

- Guava `ListenableFuture`
- JDK8 `CompletableFuture`
- 基于 Reactive Streams 的框架
  - [Reactor](/Reactor)
  - RxJava
  - ..

## Read More

- [从 Java Future 到 Guava ListenableFuture 实现异步调用](https://blog.csdn.net/pistolove/article/details/51232004)