# 	Semaphore

`Semaphore`维护了一个 信号量许可集，通过 `acquire` 消耗信号量，`release` 增加信号量，如果  `acquire`  获取信号量的时候 信号量集合中没有，就 `park` 阻塞等到。

## 如何使用

```java
int permits = 10;
// 刚开始没有许可
Semaphore semaphore = new Semaphore(0);

new Thread(() -> {
  for (int i = 0; i < permits; i++) {
    ThreadUtil.sleep(1, TimeUnit.SECONDS);
    // 偶数释放 1个，奇数释放 2个
    semaphore.release(i % 2 + 1);
  }
}).start();

for (int i = 0; i < permits; i++) {
  semaphore.acquire();
  // 获取许可后执行
  new Thread(() -> Tool.println(Thread.currentThread().getName() + "通过")).start();
}
```

```bash
> 468 : Thread-1通过
> 469 : Thread-2通过
> 469 : Thread-3通过
> 470 : Thread-4通过
> 471 : Thread-5通过
> 471 : Thread-6通过
> 472 : Thread-7通过
> 473 : Thread-8通过
> 473 : Thread-9通过
> 474 : Thread-10通过
```



## 实现原理

Semaphore **支持公平和非公平两种竞争方式**，默认是非公平的。

### Sync 基类

```java
abstract static class Sync extends AbstractQueuedSynchronizer {
  // 初始化信号量
  Sync(int permits) { setState(permits);  }
  // 获取信号量
  final int getPermits() {  return getState();  }

  // 释放可用的信号量
  protected final boolean tryReleaseShared(int releases) {
    for (;;) {
      int current = getState();
      // 当前信号量的基础上，加上新释放的信号量
      int next = current + releases;
      // CAS 设置
      ...
    }
  }
  
  // 非公平 tryAcquireShared 
  // 返回 剩余信号量，如果还有信号量，则可以执行
  final int nonfairTryAcquireShared(int acquires) {
    for (;;) {
      int available = getState();
      int remaining = available - acquires;
      if (remaining < 0 || compareAndSetState(available, remaining))
        // 剩余信号量
        return remaining;
    }
  }

...
  
}
```

### NonfairSync 非公平

只要有信号量就可以尝试执行

```java
static final class NonfairSync extends Sync {
  // 初始化信号量
  NonfairSync(int permits) { super(permits);  }

  // 返回 剩余信号量，如果还有信号量，直接执行
  // AQS 申请资源的时候，默认是非公平模式，先尝试竞争，再加入队列
  protected int tryAcquireShared(int acquires) {
    return nonfairTryAcquireShared(acquires);
  }
}
```

### FairSync 公平

先判断是否有排队，有排队就不参与竞争，入队等待机会

```java
static final class FairSync extends Sync {

  FairSync(int permits) {
    super(permits);
  }

  protected int tryAcquireShared(int acquires) {
    for (;;) {
      // 如果同步队列有排队现象，申请资源失败
      if (hasQueuedPredecessors())
        return -1;
      
      int available = getState();
      // 申请指定的资源
      int remaining = available - acquires;
      if (remaining < 0 || compareAndSetState(available, remaining))
        return remaining;
    }
  }
}
```

#### hasQueuedPredecessors

```java
// 队列中的第一个线程不是当前线程 && 当前队列中有线程
// getFirstQueuedThread() != Thread.currentThread() && hasQueuedThreads()

// 
public final boolean hasQueuedPredecessors() {
  Node t = tail;
  Node h = head;
  Node s;
  
  return h != t // 当前队列中有线程 == hasQueuedThreads()
    // 队列中的第一个线程不是当前线程
    && ((s = h.next) == null || s.thread != Thread.currentThread());
}
```



## 小结

- 公平与非公平的实现，取决自 `tryAcquireXxx`  如何实现
  - **公平**：如果有排队就入队
  - **非公平**：不管是否排队先执行



## Read More

- javadoc: [java.util.concurrent.Semaphore](https://tool.oschina.net/uploads/apidocs/jdk-zh/java/util/concurrent/Semaphore.html)
- [【JUC源码解析】Semaphore](https://www.cnblogs.com/aniao/p/aniao_semaphore.html)