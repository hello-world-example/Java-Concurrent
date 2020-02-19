# CountDownLatch

`CountDownLatch` 基于 `AQS` 同步框架，允许一个或多个线程 `await` 等待，直到指定数量(`count`) 的操作完成(`countDown`)

初始 `CountDownLatch` 时，会给定 `count`，**调用 `await` 方法时会阻塞当前线程**，直到 `count` 减小到 0，**`countDown` 会使 `count` 减 1**，该同步器**不能被重置**。

## 如何使用

### 主线程 等待 子线程

```java
int count = 3;

CountDownLatch countDownLatch = new CountDownLatch(count);

for (int i = 0; i < 3; i++) {
  int finalI = i;
  new Thread(() -> {
    ThreadUtil.sleep(finalI, TimeUnit.SECONDS);

    Tool.println(Thread.currentThread().getName() + " 完成");
    // count - 1
    countDownLatch.countDown();
  }).start();
}

Tool.println("等待 " + count + "个 任务完成");
// 阻塞当前线程
countDownLatch.await();
Tool.println(count + "个 任务都已完成!! ");
```

> ```bash
> 564 : 等待 3个 任务完成
> 564 : Thread-0 完成
> 565 : Thread-1 完成
> 566 : Thread-2 完成
> 566 : 3个 任务都已完成!! 
> ```

### 子线程 等待 主线程

```java
CountDownLatch countDownLatch = new CountDownLatch(1);

for (int i = 0; i < 3; i++) {
  new Thread(() -> {
    Tool.println(Thread.currentThread().getName() + " 等待主线程");

    try {
      countDownLatch.await(); // 等待主线程
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    Tool.println(Thread.currentThread().getName() + " 完成");
  }).start();
}

ThreadUtil.sleep(3, TimeUnit.SECONDS);
Tool.println("开始干活");
countDownLatch.countDown();
```

> ```
> 946 : Thread-0 等待主线程
> 946 : Thread-2 等待主线程
> 946 : Thread-1 等待主线程
> 949 : 开始干活
> 949 : Thread-0 开始
> 949 : Thread-2 开始
> 949 : Thread-1 开始
> ```



## 实现原理

```java
public class CountDownLatch {
  // 基于 AbstractQueuedSynchronizer 的共享资源逻辑
  private static final class Sync extends AbstractQueuedSynchronizer {

    // 初始 同步化状态，该状态的含义由子类定义
    Sync(int count) {
      setState(count);
    }

    // 基于 AbstractQueuedSynchronizer 的 共享模式
    protected int tryAcquireShared(int acquires) {
      // 如果 同步化状态 是 0，可以获取资源（执行资格），否则 park
      return (getState() == 0) ? 1 : -1;
    }

    protected boolean tryReleaseShared(int releases) {
      for (;;) {
        int c = getState();
        // 已经是 0 的话，无需处理
        if (c == 0)
          return false;
        
        // ❤ count - 1 
        int nextc = c - 1;
        // 重试 + CAS 修改 state 的值
        if (compareAndSetState(c, nextc))
          // 直到 count 到 0，才允许释放 release
          return nextc == 0;
      }
    }
  }

  ...

  // 初始化个数
  public CountDownLatch(int count) {
    if (count < 0) throw new IllegalArgumentException("count < 0");
    this.sync = new Sync(count);
  }

  // 获取共享资源，失败的话 park，通过 tryAcquireShared 的实现来控制行为
  public void await() throws InterruptedException {
    sync.acquireSharedInterruptibly(1);
  }

  // 释放资源，通过 tryReleaseShared 的实现来控制行为
  public void countDown() {
    sync.releaseShared(1);
  }
  
  ...  
}
```



## Read More

- javadoc: [java.util.concurrent.CountDownLatch](https://tool.oschina.net/uploads/apidocs/jdk-zh/java/util/concurrent/CountDownLatch.html)
- [【JUC源码解析】CountDownLatch](https://www.cnblogs.com/aniao/p/aniao_cdl.html)