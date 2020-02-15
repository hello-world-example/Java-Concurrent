
# ThreadLoad

## 简介
- ThreadLocal 实例通常是**类中的 private static final 字段**
- 数据 每个线程维护一份，**通过隔离、避免竞争**
- 线程池中每个线程是共享的，使用完需要清除，否则下次的线程使用者会使用到上次使用者设置的数据
- 个人感觉 数据是由 Thread 自己维护的，**ThreadLocal 只是给我们提供了操作 Thread 内部变量的工具**

## ThreadLocal 代码片段

```  java
ThreadLocalMap getMap(Thread t) {
  return t.threadLocals;
}

// key: ThreadLocal ; vlaue: 是存储的数据
void createMap(Thread t, T firstValue) {
  t.threadLocals = new ThreadLocalMap(this, firstValue);
}

public T get() {
  // native 方法，返回当前线程对象
  Thread t = Thread.currentThread(); 
  // 获取线程（Thread）的 threadLocals 变量，类型是 ThreadLocal.ThreadLocalMap 
  ThreadLocalMap map = getMap(t); 
  // 刚创建出来的 线程（Thread）维护的 threadLocals 变量是 null
  if (map != null) { 
    // 获取当前 ThreadLocal 对应的 value 数据
    ThreadLocalMap.Entry e = map.getEntry(this);
    if (e != null)
      return (T)e.value;
  }
  /*
   * 刚创建出来的 线程（Thread）维护的 threadLocals 变量是 null
   * 设置、获取 初始化值，当前线程 与 初始化值 进行绑定
   */
  return setInitialValue();
}



private T setInitialValue() {
  /**
   * initialValue() 是 protected 的，可以在创建 ThreadLocal 的时候进行覆盖，默认返回 null
   */
  T value = initialValue();
  // native 方法，返回当前线程对象
  Thread t = Thread.currentThread(); 
  // 获取线程（Thread）的 threadLocals 变量，类型是 ThreadLocal.ThreadLocalMap 
  ThreadLocalMap map = getMap(t); 
  if (map != null)
    map.set(this, value);
  else
    /**
     * 首次创建的时候 ThreadLocalMap map 是 null
     * createMap 会 创建一个 ThreadLocalMap 赋值给线程的threadLocals变量， 
     * ThreadLocalMap 内部维护一个 Entry 数组， 
     * Entry 继承 WeakReference<ThreadLocal>, 对 持有ThreadLocal（this）弱引用
     */
    createMap(t, value); 
  
  return value;
}

/**
 * 逻辑 跟 setInitialValue() 类似
 */
public void set(T value) {
  Thread t = Thread.currentThread();
  ThreadLocalMap map = getMap(t);
  if (map != null)
    map.set(this, value);
  else
    createMap(t, value);
}

/**
 * 
 * 移除此线程局部变量当前线程的值。
 * 
 * remove之后，如果此线程局部变量随后被当前线程 读取，且这期间当前线程没有 设置其值，则将调用其 initialValue() 方法重新初始化其值。
 * 
 * 这将导致在当前线程多次调用 initialValue 方法。
 *
 * @since 1.5
 */
public void remove() {
  ThreadLocalMap m = getMap(Thread.currentThread());
  if (m != null)
    m.remove(this);
}
```

## ThreadLocal.ThreadLocalMap 代码片段

``` java
public class ThreadLocal<T> {

  void createMap(Thread t, T firstValue) {
    t.threadLocals = new ThreadLocalMap(this, firstValue);
  }

  static class ThreadLocalMap {

    /**
     * ThreadLocalMap 
     */
    static class Entry extends WeakReference<ThreadLocal> {
      Object value;
      Entry(ThreadLocal k, Object v) {
        super(k);
        value = v;
      }
    }

    ThreadLocalMap(ThreadLocal firstKey, Object firstValue) {
      table = new Entry[INITIAL_CAPACITY]; // INITIAL_CAPACITY = 16 
      /*
       * threadLocalHashCode 在ThreadLocal被实例化的时候创建 （private final int threadLocalHashCode = nextHashCode();） 
       * 每创建一个 ThreadLocal 实例，新创建的ThreadLocal实例局变量threadLocalHashCode值 都在上次创建的基础上 加 HASH_INCREMENT(1640531527)(16进制 0x61c88647)
       */
      int i = firstKey.threadLocalHashCode & (INITIAL_CAPACITY - 1); // 类似于  101010011101100 & 1111 = 1100
      table[i] = new Entry(firstKey, firstValue);
      size = 1;
      setThreshold(INITIAL_CAPACITY);
    }

  }

}
```

## 关系图

![ThreadLocal 内部关系](../images/ThreadLocal.jpg)


## Read More

- [类 `ThreadLocal<T>`](http://tool.oschina.net/uploads/apidocs/jdk-zh/java/lang/ThreadLocal.html) 中文文档
- [ThreadLocal可能引起的内存泄露](http://www.cnblogs.com/onlywujun/p/3524675.html)
- [StackOverflow threadlocal 相关问题](https://stackoverflow.com/search?q=threadlocal)




## 神奇的数字 0x61c88647 (1640531527)(HASH_INCREMENT)


计算方式 `(Math.sqrt(5)-1) * Math.pow(2,31) - Math.pow(2,32)` ≈ **`0.618 * Math.pow(2,32) - Math.pow(2,32)`**


- [ThreadLocal 和神奇的数字 0x61c88647](http://www.cnblogs.com/ilellen/p/4135266.html)
- [斐波那契数列](https://baike.baidu.com/item/%E6%96%90%E6%B3%A2%E9%82%A3%E5%A5%91%E6%95%B0%E5%88%97#5_2)
