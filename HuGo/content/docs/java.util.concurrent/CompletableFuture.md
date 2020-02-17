# CompletableFuture

Java8 带来了 `CompletableFuture`，一个 `Future` 的扩展类，极大丰富了 `Future` 的功能，帮助我们**简化异步编程的复杂性**，并且提供了函数式编程的能力。

## 构造方式 API

| 常用方法                         | 描述                                                         |
| -------------------------------- | ------------------------------------------------------------ |
| `supplyAsync(Supplier<U>)`       | 有返回值的任务。<br />多核下默认使用 `ForkJoinPool`线程池，也可以自行构造 |
| `runAsync(Runnable)`             | 无返回值的任务。<br />多核下默认使用 `ForkJoinPool`线程池，也可以自行构造 |
|                                  |                                                              |
| `allOf(CompletableFuture<?>...)` | 所有 `CompletableFuture` 完成代表完成                        |
| `anyOf(CompletableFuture<?>...)` | 任意一个 `CompletableFuture` 完成代表完成                    |
|                                  |                                                              |
| `completedFuture(U value)`       | 直接获取 **完成状态** 的 `CompletableFuture`                 |

## 任务关系 API

### 概述

- `thenAccept` ： 任务执行完的 **回调函数**，参数是任务的执行结果
  - `thenAcceptBoth`： 两个任务同时执行完的 **回调函数**，`thenAccept` 是单个任务
  - `acceptEither`： 第一个 或 第二个 任务执行完的 **回调函数**
- `thenRun`：任务执行完的 **回调函数**，接收 `Runnable`，无法获取返返回结果
- `thenApply`： 对上一个任务的结果进行的**中间计算**，返回值传往下传递
  - `applyToEither`：第一个 或 第二个 任务执行完的 **回调函数**，返回值传往下传递
- `thenCompose`： 与 `thenApply` 作用类似，不过返回值被 `CompletableFuture` 包装
- `thenCombine`： 与 `thenAcceptBoth` 类似，不过有有返回值



### thenAccept(Consumer) / thenRun(Runnable)

```java
final CompletableFuture<Integer> completableFuture = CompletableFuture.supplyAsync(() -> {
  ThreadUtil.sleep(1, TimeUnit.SECONDS);
  return 1;
});

// ❤ CompletableFuture<Void>
CompletableFuture<Void> future = completableFuture.thenRun(() -> System.out.println("asd"));
future = completableFuture.thenAccept((data) -> System.out.println(data));

completableFuture.get(); // 等待任务执行结束
```

### thenAcceptBoth(CompletionStage, BiConsumer) / acceptEither(CompletionStage, Consumer)

```java
final CompletableFuture<Integer> taskFuture1 = CompletableFuture.supplyAsync(() -> {
  ThreadUtil.sleep(1, TimeUnit.SECONDS);  
  return 1;
});

final CompletableFuture<Integer> taskFuture2 = CompletableFuture.supplyAsync(() -> {
  ThreadUtil.sleep(2, TimeUnit.SECONDS);
  return 2;
});

// 两个都完成时的回调
final CompletableFuture<Void> acceptBothFuture = taskFuture1.thenAcceptBoth(taskFuture2, (t1, t2) -> {
  System.out.println("t1 + t2 = " + (t1 + t2));
});

long start = System.currentTimeMillis();
acceptBothFuture.get();
System.out.println("耗时: " + (System.currentTimeMillis() - start));

// ----
// 其中一个完成即回调
final CompletableFuture<Void> resultFuture = taskFuture1.acceptEither(taskFuture2, (someone) -> {
  System.out.println("t1 or t2 -> " + someone);
});

start = System.currentTimeMillis();
resultFuture.get();
// 因为两个 Future 都已经是完成状态，无需计算，直接获取结果，没有耗时
System.out.println("耗时: " + (System.currentTimeMillis() - start));
```

> t1 + t2 = 3  
> 耗时: 2002    
>
> t1 or t2 -> 1  
> 耗时: 0              

### thenApply(Function)

承上启下的任务

```java
final CompletableFuture<Integer> taskFuture1 = CompletableFuture.supplyAsync(() -> {
    ThreadUtil.sleep(1, TimeUnit.SECONDS);
    return 1;
});

final CompletableFuture<Void> future = taskFuture1
        .thenApply((d) -> d + d)
        .thenAccept(System.out::println);

future.get();
```

## 健壮性 API

### getNow

```java
final CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
    ThreadUtil.sleep(1, TimeUnit.SECONDS);
    return 1;
});

// 如果任务已经结束，返回任务结果，如果没有完成，返回默认值，这里输出2
System.out.println(future.getNow(2));
// 阻塞等待任务执行完，这里输出 1
System.out.println(future.get());
```

### complete

- 如果任务还**未完成**，强制设置任务完成，并设置任务结果，返回 true
- 如果任务**已经完成**，该操作没有效果，返回 false

```java
final CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
  ThreadUtil.sleep(1, TimeUnit.SECONDS);
  return 1;
});
// 这里任务还未执行完，强制设置任务完成，结果为 2，
System.out.println(future.complete(2));
// 非阻塞，立即输出2
System.out.println(future.get());
```

### completeExceptionally

与 `complete` 功能类似，一个设置值，一个设置异常

```java
final CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
  ThreadUtil.sleep(1, TimeUnit.SECONDS);
  return 1;
});

// 这里任务还未执行完，强制设置任务完成，使调用 get 的时候抛异常
System.out.println(future.completeExceptionally(new Exception("强制异常")));
// 这里抛出异常
System.out.println(future.get());
```

### obtrudeValue / obtrudeException

- `complete` / `completeExceptionally`，在任务已经完成的时候设置无效
- 这个两个对应方法，无论任务是否完成都有效

### exceptionally

指定出现异常时的默认值

```java
final CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
  ThreadUtil.sleep(1, TimeUnit.SECONDS);
  return 1 / 0; // exception
});

final CompletableFuture<Integer> future2 = CompletableFuture.supplyAsync(() -> {
  ThreadUtil.sleep(2, TimeUnit.SECONDS);
  return 2;
});

final CompletableFuture<Void> result = future
  // exception 默认 0
  .exceptionally(ex -> {
    ex.printStackTrace();
    return 0;
  })
  .thenAcceptBoth(future2, (t1, t2) -> {
    // 结果是 0 + 2 = 2
    System.out.println(t1 + t2);
  });

result.join();
```

### handle(BiFunction<T, Throwable, U>)

类似于 `thenApply` 和 `exceptionally` 的结合，不管上个任务是 **正常** 或 **异常**，都会执行该方法

### whenComplete(BiConsumer<T, Throwable>)

类似于 `thenAccept` 和 `exceptionally` 的结合，不管任务是 **正常** 或 **异常**，都会执行该方法



## Read More

- [`java.util.concurrent.CompletableFuture<T>`](http://www.matools.com/file/manual/jdk_api_1.8_google/java/util/concurrent/CompletableFuture.html)
- IBM Developer [通过实例理解 JDK8 的 CompletableFuture](https://www.ibm.com/developerworks/cn/java/j-cf-of-jdk8/index.html)
- [CompletableFuture API 详解](https://www.jianshu.com/p/558b090ae4bb)
- [JUC源码解析 - CompletableFuture](https://www.cnblogs.com/aniao/p/aniao_cf.html)

