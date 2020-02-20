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

## 堆排序 TODO

### 二叉堆

- 小顶堆：父结点  <= 子节点；大顶堆：父结点  >= 子节点；
- 每个结点的左子树和右子树都是一个二叉堆

```bash
  # 小顶堆 逻辑结构
        10
      ↙    ↘
   15        56
 ↙   ↘     ↙
25   30    70

# 存储结构
[10][15][56][25][30][70]
__0___1___2___3___4___5
```



- 数组的**第一个节点是堆顶**

- 节点 `i` 的 **父节点**：`(i – 1) / 2` | `(i - 1) >> 1`

- 节点 `i` 的 **左节点**：`2 * i + 1` | `i << 1 + 1`

  - 第一个节点的左节点 = `2*0+1 = 1` 即 `15`

- 节点 `i` 的 **右节点**：`2 * i + 2` | `i << 1 + 2`

  - 第一个节点的右节点 = `2*0+2 = 2` 即 `56`

  



## Read More

- [【JUC源码解析】PriorityBlockingQueue](https://www.cnblogs.com/aniao/p/aniao_pbq.html)

