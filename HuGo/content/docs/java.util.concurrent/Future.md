# Future

> en [java.util.concurrent.Future](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/Future.html)
>
> zh [java.util.concurrent.Future](https://tool.oschina.net/uploads/apidocs/jdk-zh/java/util/concurrent/Future.html)

## Future 接口

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
	- RunnableFuture (java.util.concurrent)
		- **FutureTask** (java.util.concurrent)
	- **ForkJoinTask** (java.util.concurrent)
	- **CompletableFuture** (java.util.concurrent)
	- ...

## 缺点

虽然 JDK5 新增的 `Future` 用于描述一个异步计算的结果，但是对于结果的获取却是很不方便，只能通过 **阻塞** 或者 **轮询** 的方式得到任务的结果。

- **阻塞的方式显然和我们的异步编程的初衷相违背**
- **轮询的方式又会耗费无谓的 CPU 资源，而且也不能及时地得到计算结果**

## 改进方案

- Guava `ListenableFuture`
- JDK8 `CompletableFuture`



## Read More

- [从 Java Future 到 Guava ListenableFuture 实现异步调用](https://blog.csdn.net/pistolove/article/details/51232004)