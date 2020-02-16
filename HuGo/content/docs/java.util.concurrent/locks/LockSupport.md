# LockSupport

> zh [java.util.concurrent.locks.LockSupport](https://tool.oschina.net/uploads/apidocs/jdk-zh/java/util/concurrent/locks/LockSupport.html) 官方文档的描述也很清楚



- `LockSupport` 是基于 `sun.misc.Unsafe` 封装的 **线程阻塞工具类**
- 主要提供 `park` 和 `unpark` ，即 **阻塞** 和 **解除阻塞** 两类功能
- 类似于 `Object` 的 **wait** 和 **notify** ，但是**不需要获取 锁对象**，也不会抛出中断异常
- 类似于 `Thread` 的 ~~**suspend**~~ 和 **~~resume~~** 过时方法，**不会有产生死锁的副作用**
-  `park` 和 `unpark` 可以理解为 **操作线程的许可标示**（true:许可 / false:不许可）
  - 一个线程只有一个许可，默认是 false，调用  `park`  直接阻塞
  - 假如先调动  `unpark` ，则线程的许可为 true，调用  `park`  直接返回，不阻塞
  - 因为一个线程只有一个许可，调用多次  `unpark` 与1次的效果是一样
  - 假如一开始没有 许可，调用  `park`  阻塞，需要调用   `unpark`  给予许可通行，解除阻塞
  - 解除通行 `park` 通过后 许可又变为 禁止，下次  `park`  曾然阻塞

## 一些示例

```java
// 假如先调动 unpark ，则线程的许可为 true，调用 park 直接返回，不阻塞
{
    LockSupport.unpark(Thread.currentThread());
    System.out.println("❤ 这里直接通过");
    LockSupport.park();
    System.out.println("end");
}

// 调用多次 unpark 与1次的效果是一样
// 解除通行后 许可 变为 禁止，下次  park  曾然阻塞
{
    LockSupport.unpark(Thread.currentThread());
    LockSupport.unpark(Thread.currentThread());
  
    System.out.println("这里直接通过");
    LockSupport.park();
    System.out.println("❤ 这里仍然会阻塞");
    LockSupport.park();
    System.out.println("end");
}

{
    final Thread threadMain = Thread.currentThread();
    new Thread(() -> {
        try { Thread.sleep(2000); } catch (InterruptedException e) {  }
        // 2s 后为主线程解锁（解除阻塞）
        System.out.println("unpark" + System.currentTimeMillis());
        LockSupport.unpark(threadMain);
    }).start();

    System.out.println("park  " + System.currentTimeMillis());
    LockSupport.park();
    System.out.println("ok    " + System.currentTimeMillis());
  
  // 输出
  park   ...1520
  unpark ...3526
  ok     ...3526
}
```

## park / park(Object blocker)

程序启动之后，通过 `jps` 找到对应的 java 进程 id，`jcmd <pid> Thread.print`  打印线程栈信息

```java
$ jps  
$ jcmd <pid> Thread.print
  
{
  /**
   * "main" #1 prio=5 os_prio=31 tid=0x00007fb4e6802000 nid=0x1103 waiting on condition [0x0000700009ca2000]
   *    java.lang.Thread.State: WAITING (parking)
   *         at sun.misc.Unsafe.park(Native Method)
   *         at java.util.concurrent.locks.LockSupport.park(LockSupport.java:304)
   *         at xyz.kail.demo.java.concurrent.locks.LockSupportParkMain.main(LockSupportParkMain.java:8)
   */
  LockSupport.park();
}

{
  /**
   * "main" #1 prio=5 os_prio=31 tid=0x00007f7fc8002000 nid=0xd03 waiting on condition [0x0000700001b1d000]
   *    java.lang.Thread.State: WAITING (parking)
   *         at sun.misc.Unsafe.park(Native Method)
   *         - parking to wait for  <0x000000076ada4748> (a java.util.HashMap)
   *         at java.util.concurrent.locks.LockSupport.park(LockSupport.java:175)
   *         at xyz.kail.demo.java.concurrent.locks.LockSupportParkMain.main(LockSupportParkMain.java:18)
   */
  LockSupport.park(new HashMap<>());
}
```

可以看到 区别在于 `park(Object blocker)` 会多一行 `- parking to wait for  <0x000000076ada4748> (a java.util.HashMap)` 显示 `blocker` 的类。

 `blocker` 对象会保存在 `Thread` 对象的 `volatile Object parkBlocker` 属性中，可通过 `LockSupport.getBlocker(thread)` 获取。



