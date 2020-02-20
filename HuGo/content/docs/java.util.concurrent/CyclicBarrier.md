# CyclicBarrier

## 如何使用

```java
CyclicBarrier cyclicBarrier = new CyclicBarrier(3);

// 随机休息一段时间
Runnable work = () -> {
  final int sleep = Math.abs(new Random().nextInt() % 10);
  Tool.println("sleep " + sleep);
  ThreadUtil.sleep(sleep, TimeUnit.SECONDS);
};

// 调用 barrier.await()
Consumer<CyclicBarrier> await = (CyclicBarrier barrier) -> {
  try {
    barrier.await();
  } catch (Exception e) {
    e.printStackTrace();
  }
};

final Runnable runnable = () -> {
  work.run();
  await.accept(cyclicBarrier); // 等待所有线程准备好
  Tool.println("A ");

  work.run();
  await.accept(cyclicBarrier); // 等待所有线程准备好
  Tool.println("B");
};

new Thread(runnable).start();
new Thread(runnable).start();
new Thread(runnable).start();
```

```bash
> 566 : Thread-2 : sleep 3
> 566 : Thread-1 : sleep 2
> 566 : Thread-0 : sleep 1
> 569 : Thread-2 : ---A--- // 3 秒后同时执行
> 569 : Thread-0 : ---A---
> 569 : Thread-1 : ---A---
> // 
> 569 : Thread-2 : sleep 6
> 569 : Thread-0 : sleep 6
> 569 : Thread-1 : sleep 5
> 575 : Thread-0 : ---B---  // 6 秒后同时执行
> 575 : Thread-2 : ---B---
> 575 : Thread-1 : ---B---
```



## 实现原理

**CyclicBarrier 并非直接基于 AQS， 而是基于 ReentrantLock 和 Condition**

- 所有线程的 await 操作 都是 lock() 操作，获取到锁之后，凭证数减1
- 如果不到 0 ，就 `lock.condition.await` ，等待被唤醒
- 如果到 0 了，继续执行并 `lock.condition.signalAll`，唤醒所有等待的线程继续执行
- 并重新开始计数

### await

```java
public int await() throws InterruptedException, BrokenBarrierException {
  try {
    return dowait(false, 0L);
  } catch (TimeoutException toe) {
    throw new Error(toe); // cannot happen
  }
}
```

### dowait

```java
private int dowait(boolean timed, long nanos)  throws ... {
  final ReentrantLock lock = this.lock;
  lock.lock();
  try {
    final Generation g = generation;
    ...

    int index = --count;
    // ❤❤❤ index 减到 0，从新开始下一次计数 ❤❤❤
    if (index == 0) {  // tripped
      boolean ranAction = false;
      try {
        ...
        nextGeneration();
        return 0;
      } finally {
        if (!ranAction)
          breakBarrier();
      }
    }

    for (;;) {
      try {
        if (!timed)
          // ❤❤❤ await 释放锁 ❤❤❤
          trip.await();
        else if (nanos > 0L)
          // ❤❤❤ await 释放锁 ❤❤❤
          nanos = trip.awaitNanos(nanos);
      } catch (InterruptedException ie) {
        ...
      }
      ...
    }
  } finally {
    lock.unlock();
  }
}
```

### breakBarrier

```java
private void breakBarrier() {
  // 重置栅栏
  // 线程中断后需要 reset(), 否则会抛出 BrokenBarrierException 异常
  generation.broken = true;
  // 重置计数
  count = parties;
  // 唤醒所有 持有锁并等待的线程
  trip.signalAll();
}
```

### nextGeneration

```java
private void nextGeneration() {
  // 唤醒所有 持有锁并等待的线程
  trip.signalAll();
  // 重置计数
  count = parties;
  // generation.broken = false;
  generation = new Generation();
}
```



## Read More

- [java.util.concurrent.CyclicBarrier](https://tool.oschina.net/uploads/apidocs/jdk-zh/java/util/concurrent/CyclicBarrier.html)
- [【JUC源码解析】CyclicBarrier](https://www.cnblogs.com/aniao/p/aniao_cyclicbarrier.html)

