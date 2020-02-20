# SynchronousQueue

`SynchronousQueue` 是一个 **没有长度** 的阻塞队列，**全程无锁，全部基于 CAS**

每个插入操作必须等待另一个线程的对应移除操作，即 `put` 操作会阻塞，直到其他线程 `take`。适用场景：

- 针对**一个数据变化的通知**
- 生产一个数据，消费一个数据
- ...

## 如何使用

```java
SynchronousQueue<Integer> sync = new SynchronousQueue<>();

final int[] someData = new int[1];
Consumer<Integer> changeIt = (newData) -> someData[0] = newData;
Supplier<Integer> getChange = () -> someData[0];

////////////////////////////////
new Thread(() -> {
  for (; ; ) {
    final int newData = Math.abs(new Random().nextInt() % 100);
    changeIt.accept(newData);
    
    try {
      Tool.println("我变了 " + newData);
      // 这里会 park，直到 sync.take
      sync.put(newData);
      ThreadUtil.sleep(newData * 100 / 2);
    } catch (InterruptedException e) { ... }

    // 结束条件
    if (newData < 30) { break; }
  }
}).start();

////////////////////////////////
new Thread(() -> {
  for (; ; ) {
    try {
      
      final Integer data = sync.take();
      Tool.println("知道了 " + getChange.get());
      // 结束条件
    	if (newData < 30) { break; }
    } catch (InterruptedException e) { ... }
  }
}).start();
```

``` bash
> 549 : Thread-0 : 我变了 64
> 549 : Thread-1 : 知道了 64
> 553 : Thread-0 : 我变了 14
> 553 : Thread-1 : 知道了 14
```



## Read More

- [【JUC源码解析】SynchronousQueue](https://www.cnblogs.com/aniao/p/aniao_sq.html)