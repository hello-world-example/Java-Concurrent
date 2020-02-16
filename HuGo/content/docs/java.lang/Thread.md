# Thread



## 线程状态

> @see [java.lang.Thread.State](https://tool.oschina.net/uploads/apidocs/jdk-zh/java/lang/Thread.State.html)

1. `NEW`  至今尚未启动的线程处于这种状态，`new Thread`
2. `RUNNABLE`  正在 Java 虚拟机中执行的线程处于这种状态，`new Thread().start()`
3. `BLOCKED`  受阻塞并等待某个监视器锁的线程处于这种状态，`synchronized(lock){}`
4. `WAITING`  无限期地等待另一个线程来执行某一特定操作的线程处于这种状态，`synchronized(lock){ lock.wait() }`
5. `TIMED_WAITING`  等待另一个线程来执行取决于指定等待时间的操作的线程处于这种状态，，`synchronized(lock){ lock.wait(timeout) }` 、`Thread.sleep(timeout)`
6. `TERMINATED`  线程执行结束



## Methods 简介

### thread.join()

等待线程执行完毕，再向下执行，与主线程的关系变成 **串行执行**。

```java
final Thread thread = new Thread(() -> System.out.println(System.currentTimeMillis()));
thread.start();
// 等待 thread 线程执行完毕， 再执行下面的代码
thread.join();
System.out.println("Main 方法结束");
```

### Thread.yield()

使 *当前* 线程从**执行状态** 变为 **就绪状态**，CPU 重新从众多  **就绪状态** 的线程里选择，**包括当前线程**。

- 优先级高的**不是一定被执行**
- 当前线程**不是已经不被执行**
- 所有 就绪状态的线程，**都有可能被执行**
- 情况取决于 CPU 的选择，**结果是随机的**

### thread.interrupt()

中断操作只是一个标志位，并不会立即终止当前线程，需要自身在业务逻辑中判断 `thread.isInterrupted()`，并确定是否执行后续的代码。

### Thread.interrupted() / thread.isInterrupted()

用于判断当前线程是否被标记为中断，需要注意的是这两个方法的区别

```java
// Thread.interrupted();
// ❤❤❤ 静态方法 重置中断状态 ❤❤❤
public static boolean interrupted() {
  return currentThread().isInterrupted(true);
}

// Thread.currentThread().isInterrupted();
// ❤❤❤ 实例方法 不重置中断状态 ❤❤❤
public boolean isInterrupted() {
  return isInterrupted(false);
}

/**
 * 判断线程是否中断，ClearInterrupted 用于控制是否重置中断状态
 */
private native boolean isInterrupted(boolean ClearInterrupted);
```

除此之外，**产生 `InterruptedException` 后，也会清除中断状态**。

> 一个比较合理的解释是：中断应该只被处理一次，catch 了 InterruptedException，说明能处理这个异常，不希望上层调用者看到这个中断，详见：[interrupted 和 isInterrupted 的区别](https://blog.csdn.net/hj7jay/article/details/53462553)

### thread.isAlive()

如果线程已经启动且尚未终止，则为活动状态

| Status                  | isAlive() |
| ----------------------- | --------- |
| NEW                     | **false** |
| RUNNABLE                | true      |
| BLOCKED                 | **true**  |
| WAITING / TIMED_WAITING | **true**  |
| TERMINATED              | false     |

### UncaughtExceptionHandler

线程内部的运行时异常，不能在线程 外部 `try...catch...` 获取，如果需要自定义处理，需要设置异常处理器

```java
// 线程全局异常 处理器
Thread.setDefaultUncaughtExceptionHandler((t, e) -> System.out.println("[System]线程 " + t.getName() + " 抛出异常" + e.getMessage()));

final Thread thread = new Thread(() -> System.out.println(1 / 0));

// 实例异常捕捉器
thread.setUncaughtExceptionHandler((t, e) -> System.out.println("[Thread]线程 " + t.getName() + " 抛出异常" + e.getMessage()));

thread.start();
```

### thread.getStackTrace()

获取堆栈跟踪信息，**常用来获取调用方信息**。

```java
package xyz.kail.demo.java.concurrent.thread;

public class StackTraceThreadMain {

    public static void printStackTrace(StackTraceElement[] stackTraces) {
        for (StackTraceElement stackTrace : stackTraces) {
            System.out.println(stackTrace.toString());
        }
    }

    private static void b() {
        printStackTrace(Thread.currentThread().getStackTrace());
    }


    private static void a() {
        b();
    }

    /**
     * java.lang.Thread.getStackTrace(Thread.java:1559)
     * xyz.kail.demo.java.concurrent.thread.StackTraceThreadMain.b(StackTraceThreadMain.java:12)
     * xyz.kail.demo.java.concurrent.thread.StackTraceThreadMain.a(StackTraceThreadMain.java:17)
     * xyz.kail.demo.java.concurrent.thread.StackTraceThreadMain.main(StackTraceThreadMain.java:30)
     */
    public static void main(String[] args) {
        a();
    }
}
```

### Thread.getAllStackTraces()

以下输出信息在 Debug 的时候应该很常见

```java
public class AllStackTracesThreadMain {

    /**
     * Thread Name::: Reference Handler
     * java.lang.Object.wait(Native Method)
     * java.lang.Object.wait(Object.java:502)
     * java.lang.ref.Reference.tryHandlePending(Reference.java:191)
     * java.lang.ref.Reference$ReferenceHandler.run(Reference.java:153)
     *
     * Thread Name::: Monitor Ctrl-Break
     * java.net.PlainSocketImpl.socketCreate(Native Method)
     * java.net.AbstractPlainSocketImpl.create(AbstractPlainSocketImpl.java:109)
     * java.net.Socket.createImpl(Socket.java:457)
     * java.net.Socket.<init>(Socket.java:431)
     * java.net.Socket.<init>(Socket.java:211)
     * com.intellij.rt.execution.application.AppMainV2$1.run(AppMainV2.java:59)
     *
     * Thread Name::: Signal Dispatcher
     *
     * Thread Name::: Finalizer
     * java.lang.Object.wait(Native Method)
     * java.lang.ref.ReferenceQueue.remove(ReferenceQueue.java:143)
     * java.lang.ref.ReferenceQueue.remove(ReferenceQueue.java:164)
     * java.lang.ref.Finalizer$FinalizerThread.run(Finalizer.java:209)
     *
     * Thread Name::: main
     * java.lang.Thread.dumpThreads(Native Method)
     * java.lang.Thread.getAllStackTraces(Thread.java:1610)
     * xyz.kail.demo.java.concurrent.thread.AllStackTracesThreadMain.main(AllStackTracesThreadMain.java:8)
     */
    public static void main(String[] args) {
        final Map<Thread, StackTraceElement[]> allStackTraces = Thread.getAllStackTraces();
        allStackTraces.forEach((thread, value) -> {
            System.out.println("Thread Name::: " + thread.getName());
            StackTraceThreadMain.printStackTrace(value);
            System.out.println();

        });
    }
}

```

### Thread.dumpStack()

将当前线程的堆栈跟踪打印至标准错误流。该方法仅用于调试。

```java
public static void dumpStack() {
  new Exception("Stack trace").printStackTrace();
}
```



## 废弃的方法

| Method                   | Desc              | 废弃原因                                                     |
| ------------------------ | ----------------- | ------------------------------------------------------------ |
| `stop()`                 | **强迫** 线程停止 | 假设线程中需要执行 1/2/3/4 四个步骤，调用 `stop()` 之后，无法预知执行到哪一步了，这有**可能导致任意的行为** |
| `suspend()` / `resume()` | 暂停 / 恢复 线程  | 如果目标线程持有锁，**调用 suspend 之后，不会释放锁**<br />如果 resume 方法先于 suspend 方法调用，就会**导致死锁** |
| `destroy()`              | 无实现            | 该方法没有实现逻辑，直接抛出 `throw new NoSuchMethodError()` 异常 |
| `countStackFrames()`     | 计算堆栈帧数      | 线程必须挂起，依赖于 `suspend()`                             |

> 为何不赞成使用 `Thread.stop`、`Thread.suspend` 和 `Thread.resume` ？
>
> - [中文 译文](https://blog.csdn.net/loongshawn/article/details/53034176)
> - [英文 原文](https://docs.oracle.com/javase/1.5.0/docs/guide/misc/threadPrimitiveDeprecation.html)

### 



