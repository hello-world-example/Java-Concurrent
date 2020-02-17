# ExecutorCompletionService

`ExecutorCompletionService` 是一个**获取异步任务结果的工具类**。

封装了 `Executor` 和 `BlockingQueue`，把已经完成的任务放入队列，提供从队列中获取结果的方法 `take` / `poll`。

## 源码摘录

```java
public class ExecutorCompletionService<V> implements CompletionService<V> {

  private final Executor executor;
  private final BlockingQueue<Future<V>> completionQueue;
  
  private class QueueingFuture extends FutureTask<Void> {
    private final Future<V> task;
    QueueingFuture(RunnableFuture<V> task) {
      super(task, null);
      this.task = task;
    }
    // ❤ FutureTask 执行完成后，会回调 Done 方法
    protected void done() { 
      // 加入队列
      completionQueue.add(task); 
    }
  }
  
  public Future<V> take() throws InterruptedException {
    return completionQueue.take();
  }

  public Future<V> poll() {
    return completionQueue.poll();
  }
}
```

## 使用示例

```java
ExecutorService threadPool = Executors.newCachedThreadPool();
// 包装 threadPool
CompletionService<Long> completionService = new ExecutorCompletionService<>(threadPool);

// 提交三个任务
completionService.submit(() -> ThreadUtil.sleep(1, TimeUnit.SECONDS), 1L);
completionService.submit(() -> ThreadUtil.sleep(2, TimeUnit.SECONDS), 2L);
completionService.submit(() -> ThreadUtil.sleep(3, TimeUnit.SECONDS), 3L);

for (; ; ) {
  // 哪个先执行完，就先获取哪个任务的结果
  // 如果没有执行完的任务，这里会 阻塞
  System.out.println(completionService.take().get());
}
```

## Read More

> [`java.util.concurrent.ExecutorCompletionService<V>`](https://tool.oschina.net/uploads/apidocs/jdk-zh/java/util/concurrent/ExecutorCompletionService.html)