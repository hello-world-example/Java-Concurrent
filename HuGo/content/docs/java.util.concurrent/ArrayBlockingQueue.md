# ArrayBlockingQueue

> @see [BlockingQueue](../BlockingQueue)

- ArrayBlockingQueue 是 [BlockingQueue](../BlockingQueue) 的一个基于数组的实现
- 基于 `ReentrantLock` 的 `await` 和 `signal` 通知机制 实现阻塞
- 操作队列数据的时候 并**不像 ArrayList 一样，需要扩容 或 数据平移带来的性能损耗**
- 仅仅依靠平移 `takeIndex` / `putIndex` 读写坐标

## 实现原理

### ArrayBlockingQueue 构造

```java
// 队列元素
final Object[] items;

// 读取坐标
int takeIndex;

// 写入坐标
int putIndex;

// 当前数据个数
int count;

public ArrayBlockingQueue(int capacity, boolean fair) {
  if (capacity <= 0)
    throw new IllegalArgumentException();

  this.items = new Object[capacity];

  // 基于 Lock 的 await 和 signal 通知机制
  lock = new ReentrantLock(fair);

  // 实例化了两个 Condition 队列，用于区分 获取 和 添加
  // 唤醒的时候可以唤醒 指定类型的操作
  notEmpty = lock.newCondition();
  notFull =  lock.newCondition();
}
```
### put 阻塞

```java
public void put(E e) throws InterruptedException {
  checkNotNull(e);
  final ReentrantLock lock = this.lock;
  // 如果当前线程未被中断，则获取锁
  lock.lockInterruptibly();
  try {
    // 队列已经满了
    while (count == items.length)
      // park 阻塞
      notFull.await();
    
    // 入队
    enqueue(e);
  } finally {
    lock.unlock();
  }
}
```

### enqueue 入队

```java
private void enqueue(E x) {
  final Object[] items = this.items;
  items[putIndex] = x;
  // 如果下标已经移动到最后一位，从头开始。 
  // 相当于下标循环移动，只要 takeIndex 不超过 putIndex，就不会出现同一个数据读两次的情况
  // putIndex 也不会超过 takeIndex，因为入队之前做了 count 队里数据个数的判断
  if (++putIndex == items.length)
    putIndex = 0;
  
  // 统计队列元素个数
  count++;

  // 如果有数据了，唤醒因为 take 没有数据 阻塞的线程
  notEmpty.signal();
}
```

### take 阻塞

```java
public E take() throws InterruptedException {
  final ReentrantLock lock = this.lock;
  // 如果当前线程未被中断，则获取锁
  lock.lockInterruptibly();
  try {
    // 队列空了
    while (count == 0)
      // park 阻塞
      notEmpty.await();
    
    // 出队
    return dequeue();
  } finally {
    lock.unlock();
  }
}
```

### dequeue 出队

```java
private E dequeue() {
  final Object[] items = this.items;
  E x = (E) items[takeIndex];
  // 当前坐标数据制空
  items[takeIndex] = null;
  // 如果下标已经移动到最后一位，从头开始。 
  // 相当于下标循环移动，只要 takeIndex 不超过 putIndex，就不会出现同一个数据读两次的情况
  // putIndex 也不会超过 takeIndex，因为入队之前做了 count 队里数据个数的判断
  if (++takeIndex == items.length)
    takeIndex = 0;
	// 统计队列元素个数
  count--;
  
  // 迭代器操作
  if (itrs != null)
    itrs.elementDequeued();
  
  // 如果数据减少了，唤醒因为队列满了 put 阻塞的线程
  notFull.signal();
  return x;
}
```

### 其它操作

- `offer` / `poll` ： 同样加锁， 只是到达队列的边界条件时**不 `await`**，而是直接**返回特殊值**
- `add` / `remove` ： 内部调用 `offer` / `poll` ，**根据其返回值判断是否抛出异常**
- `offer(time)` / `poll(time)` ：**几乎与 `put` / `take` 一样**，只是 **调用 await 的有时间参数的方式**



