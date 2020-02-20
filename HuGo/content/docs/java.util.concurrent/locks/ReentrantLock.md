# ReentrantLock

## 实现原理

### Sync

```java
// 加锁 和 释放的过程，都是都 对 state(重入次数) 的维护
abstract static class Sync extends AbstractQueuedSynchronizer {

  abstract void lock();

  // 非公平锁的实现
  final boolean nonfairTryAcquire(int acquires) {
    final Thread current = Thread.currentThread();
    int c = getState();
    
    //【抢占锁】 为 0 时说明没有线程持有锁，
    if (c == 0) {
      // CAS 自旋竞争
      if (compareAndSetState(0, acquires)) {
        // 成功后，独占线程设置为自己
        setExclusiveOwnerThread(current);
        return true;
      }
    }
    // 【重入锁】不为 0 时判断独占线程是否自己， 是的话重入
    else if (current == getExclusiveOwnerThread()) {
      // 重入次数 + 1
      int nextc = c + acquires;
      if (nextc < 0) // overflow
        throw new Error("Maximum lock count exceeded");
      setState(nextc);
      return true;
    }
    // 竞争失败，排队
    return false;
  }

  protected final boolean tryRelease(int releases) {
    // 减少重入次数
    int c = getState() - releases;
    if (Thread.currentThread() != getExclusiveOwnerThread())
      throw new IllegalMonitorStateException();
    

    boolean free = false;
    if (c == 0) {
      // 减到 0 的时候清空独占线程
      free = true;
      setExclusiveOwnerThread(null);
    }
    setState(c);
    return free;
  }

  // 用到 Condition 需要实现该方法
  protected final boolean isHeldExclusively() {
    // 当前独占线程是否持有锁
    return getExclusiveOwnerThread() == Thread.currentThread();
  }
  
  ...
}
```
### NonfairSync 非公平

```java
static final class NonfairSync extends Sync {

  final void lock() {
    // CAS 竞争把 State 从 0 设置为 1
    if (compareAndSetState(0, 1))
      // 成功，标记当前线程独占
      setExclusiveOwnerThread(Thread.currentThread());
    else
      // tryAcquire + enQueue 入队
      acquire(1);
  }

  protected final boolean tryAcquire(int acquires) {
    return nonfairTryAcquire(acquires);
  }
}
```
### FairSync 公平

```java
static final class FairSync extends Sync {

  final void lock() {
    acquire(1);
  }

  protected final boolean tryAcquire(int acquires) {
    final Thread current = Thread.currentThread();
    int c = getState();
    if (c == 0) {
      // 比非公平锁，多了一步 !hasQueuedPredecessors()，判断是否有排队
      if (!hasQueuedPredecessors() && compareAndSetState(0, acquires)) {
        setExclusiveOwnerThread(current);
        return true;
      }
    }
    // 与非公平锁一样
    else if (current == getExclusiveOwnerThread()) {
      int nextc = c + acquires;
      if (nextc < 0)
        throw new Error("Maximum lock count exceeded");
      setState(nextc);
      return true;
    }
    return false;
  }
}
```

## 小结

- `ReentrantLock` 的 非公平锁**并非严格意义上非公平锁**，**只是没有线程持有锁的时候，这时进行 CAS 是随机的**
- 一旦有线程排队现象，获取锁的时候直接排队
- 而且释放锁的时候也是按照队列顺序释放的，**并非随机释放**

## 一个示例

```java
final ReentrantLock lock = new ReentrantLock();
final Condition condition = lock.newCondition();

// 生成 20 个数字
final ArrayList<Integer> array = new ArrayList<>();
for (int i = 0; i < 200; i++) {
  array.add(i);
}

final Runnable runnable = () -> {
  for (; ; ) {
    try {
      lock.lock();
      ThreadUtil.sleep(50, TimeUnit.MILLISECONDS);
			// 唤醒，如果 condition 队列中没有数据，则没有效果
      condition.signal();
      // 没有数据的时候结束
      if (array.isEmpty()) {
        break;
      }
			// 删除 并 输出列表中的第一条数据
      Tool.println(array.remove(0));

      try {
        // park 等待
        condition.await();
      } catch (InterruptedException e) { e.printStackTrace(); }
    } finally {
      lock.unlock();
    }
  }
};

// 
new Thread(runnable).start();
new Thread(runnable).start();
```


## Read More

- [java.util.concurrent.locks.ReentrantLock](https://tool.oschina.net/uploads/apidocs/jdk-zh/java/util/concurrent/locks/ReentrantLock.html)