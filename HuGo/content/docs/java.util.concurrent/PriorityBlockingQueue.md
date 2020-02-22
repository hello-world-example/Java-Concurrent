# PriorityBlockingQueue

- `PriorityBlockingQueue` 是一个**基于数组** 的 **无界** **优先级**队列
- 因为可以放**无限的元素**，所以**会涉及到队列的扩容**
- 队列元素有优先级基于 **堆排序**，优先级高的会放在前面
- 元素的优先级比较需要自定义 `Comparator` ，或者 元素自身是 `Comparable` 的，否则会抛出异常
  - `java.lang.ClassCastException: xxx.xxx.Xxx cannot be cast to java.lang.Comparable`



## 实现原理

### put

```java
public void put(E e) {
  // 无界队列， put 不会 阻塞
  offer(e); // never need to block
}
```

### offer

```java
public boolean offer(E e) {
  if (e == null) throw new NullPointerException();

  final ReentrantLock lock = this.lock;
  lock.lock();
  int n, cap;
  Object[] array;

  // 元素个数 >= 现有队列的长度
  while ((n = size) >= (cap = (array = queue).length))
    // 扩容 System.arraycopy(array, 0, newArray, 0, oldCap);
    tryGrow(array, cap);

  try {
    Comparator<? super E> cmp = comparator;
    
    if (cmp == null)
      // 没有自定义比较器，权限大的排前面，排序算法一样
      siftUpComparable(n, e, array);
    else
      // 使用自定义比较器，权限大的排前面，排序算法一样
      siftUpUsingComparator(n, e, array, cmp);
    
    size = n + 1;
    notEmpty.signal();
  } finally {
    lock.unlock();
  }
  return true;
}
```

### siftUpComparable 入堆

```java
private static <T> void siftUpComparable(int k, T x, Object[] array) {
  Comparable<? super T> key = (Comparable<? super T>) x;
  while (k > 0) {
    // (k - 1) / 2  即 当前节点的父节点
    int parent = (k - 1) >>> 1;
    Object e = array[parent];
    // 比父节点大，则退出
    if (key.compareTo((T) e) >= 0)
      break;

    // 比父节点小，则与之交换（小顶堆）
    array[k] = e;
    k = parent;
  }
  array[k] = key;
}
```

### take

```java
public E take() throws InterruptedException {
  final ReentrantLock lock = this.lock;
  lock.lockInterruptibly();
  E result;
  try {
    // 出队，如果没有数据 await
    while ( (result = dequeue()) == null)
      notEmpty.await();
  } finally {
    lock.unlock();
  }
  return result;
}
```

### dequeue

```java
private E dequeue() {
  int n = size - 1;
  if (n < 0)
    return null;
  else {
    Object[] array = queue;
    // 根据堆排序算法，第一个元素，优先级最高
    E result = (E) array[0];
    // 最有一个元素入堆顶，重排堆
    E x = (E) array[n];
    array[n] = null;
    
    // 调整堆数
    Comparator<? super E> cmp = comparator;
    if (cmp == null)
      siftDownComparable(0, x, array, n);
    else
      siftDownUsingComparator(0, x, array, n, cmp);
    
    size = n;
    return result;
  }
}
```

### siftDownComparable 重排堆

```java
// k = 0 = 堆顶索引
// x = 堆尾数据
// n = size = 堆尾索引
private static <T> void siftDownComparable(int k, T x, Object[] array, int n) {
  if (n > 0) {
    Comparable<? super T> key = (Comparable<? super T>)x;
    int half = n >>> 1;           // loop while a non-leaf
    // size / 2 只需要循环一半
    while (k < half) {
      // k * 2 + 1 = 左子节点
      int child = (k << 1) + 1; // assume left child is least
      Object c = array[child];
      // k * 2 + 1 = 左子节点
      int right = child + 1;
      if (right < n && ((Comparable<? super T>) c).compareTo((T) array[right]) > 0)
        c = array[child = right];
      if (key.compareTo((T) c) <= 0)
        break;
      // 找到最小节点与之交换
      array[k] = c;
      k = child;
    }
    array[k] = key;
  }
}
```

## 堆排序

算法详见： [堆排序](/Algorithms/docs/Sort/HeapSort/)

## Read More

- [【JUC源码解析】PriorityBlockingQueue](https://www.cnblogs.com/aniao/p/aniao_pbq.html)

