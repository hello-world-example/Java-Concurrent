# Exchanger

`Exchanger` 跟  [SynchronousQueue](../SynchronousQueue) 有点类似，都有点类似于线程间通讯

- `Exchanger`  在发送的时候同时能够接收
- `SynchronousQueue` 一个发送一个接收，创建两个 `SynchronousQueue` 来传递数据好像同样可以达到 `Exchanger`  的效果

## 如何使用

```java
Exchanger<Integer> exchanger = new Exchanger<>();

Runnable runnable = () -> {
  for (; ; ) {
    final int newData = Math.abs(new Random().nextInt() % 100);
    Integer oldData = 0;
    try {
      Tool.println("走你 → " + newData);
      oldData = exchanger.exchange(newData);
      Tool.println("← 收到 " + oldData);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    ThreadUtil.sleep(newData * 10);

    if (newData < 30 || oldData < 30) {
      break;
    }
  }
};

new Thread(runnable).start();
new Thread(runnable).start();
```

``` bash
>494 : Thread-1 : 给你 → 80
>494 : Thread-0 : 给你 → 46
>494 : Thread-1 : ← 收到 46
>494 : Thread-0 : ← 收到 80
>
>494 : Thread-0 : 给你 → 43
>495 : Thread-1 : 给你 → 15
>495 : Thread-1 : ← 收到 43
>495 : Thread-0 : ← 收到 15
```

## Read More

- [【JUC源码解析】Exchanger](https://www.cnblogs.com/aniao/p/aniao_exchanger.html)