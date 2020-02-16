# Unsafe

`Unsafe` 是 Java 中可以直接操作内存的工具，属于 `sun.*` 包下的API。



> **直接操作内存是很危险的一件事，不能通过Java虚拟机的垃圾回收机制进行内存释放，在使用的时候需要注意内存泄漏和溢出，并且Unsafe是一个平台相关的类，在实际开发中建议不要直接使用。** 



# 获取 Unsafe 类实例

JDK 对这个类进行了限制，我们不能通过常规 `new` 的方式去获取该类的实例，也不能通过`Unsafe.getUnsafe()`获取

```java
public final class Unsafe {

    private static final Unsafe theUnsafe;
    ...
	// 私有构造方法
    private Unsafe() {
    }

    // 不是 系统 ClassLoader 会 抛出 SecurityException 异常
    public static Unsafe getUnsafe() {
        Class var0 = Reflection.getCallerClass();
        if (!VM.isSystemDomainLoader(var0.getClassLoader())) {
            throw new SecurityException("Unsafe");
        } else {
            return theUnsafe;
        }
    }
    
    static {
        ...
        // 但是静态代码块在 类加载后 会在内部实例化 自己
        theUnsafe = new Unsafe();
        ...
    }
}
```

## 通过反射 获取 Unsafe 实例

```java
Field theUnsafeField = Unsafe.class.getDeclaredField("theUnsafe");
theUnsafeField.setAccessible(true);
Unsafe unsafe = (Unsafe) theUnsafeField.get(null);
System.out.println(unsafe);
```

## 让 引导类加载器 加载

```java
package xyz.kail.javase.demo;

import sun.misc.Unsafe;

// 使用以下命令修改 引导类路径
// java -Xbootclasspath:$JAVA_HOME/jre/lib/rt.jar:. xyz.kail.javase.demo.Main
public class Main {
    public static void main(String[] args) {
        Unsafe unsafe = Unsafe.getUnsafe();
        System.out.println(unsafe);
    }
}
```

# Unsafe API


## ❤ 线程

Unsafe提供的 **`park`、`unpark` 是整个Java并发包的基础核心操作方法，其被封装成的 `LockSupport` 本质最终还是调用的 Unsafe**

- `void park(boolean isAbsolute, long time);`  挂起线程
  - isAbsolute 是否是绝对时间
    - true 绝对时间，超时时间参数time的单位是**毫秒**，一般用当前时间的毫秒数+超时时间毫秒数作为参数
    - false 相对时间，超时时间参数time的单位是**纳秒**，time为0表示一直阻塞，没有超时时间
- `void unpark(Object thread);` 终止一个挂起的线程，使其恢复正常

## ❤ 字段

- `long objectFieldOffset(Field var1);` 对象字段偏移量
- `long staticFieldOffset(Field var1);` 静态字段偏移量
- `Object staticFieldBase(Field var1);` 静态字段所对应的类，读写静态字段需要通过该API获取类


## ❤ CAS 操作（compareAndSwap）

- 参数
  - obj 被操作的对象
  - offset 对象字段的偏移
  - expected 当前期待的值
  - newValue 新值
- 如果当前值是 expected 则设置新值 newValue 并返回 true，否则返回false

- boolean compareAndSwapXxx(obj, offset, expected, newValue)
  - boolean compareAndSwapObject(Object, long, Object, Object);
  - boolean compareAndSwapInt(Object, long, int, int);
  - boolean compareAndSwapLong(Object, long, long, long);

- getAndAddXxx(obj, offset, addValue)
  - getAndAddInt()
  - getAndAddLong()
- getAndSetXxx(obj, offset, setValue)
  - getAndAddInt()
  - getAndSetLong()
  - getAndSetObject()




## 内存信息

- `int addressSize();` 操作系统的直接字节长度，32位是4，64位是8
- `int pageSize();` 操作系统的内存页大小，这个值永远都是2的幂次方  

## 不调用构造方法创建类对象

```java
public native Object allocateInstance(Class<?> clazz) throws InstantiationException;
```

## 定义类

```java
public native Class<?> defineClass(String className, byte[] b, int offset, int len, 
                                   ClassLoader loader, ProtectionDomain protectionDomain);

// 定义一个匿名类
public native Class<?> defineAnonymousClass(Class<?> targetClass, byte[] classBytes, 
                                            Object[] cpPatches);

```


## 数组

- `int arrayBaseOffset(Class<?> arrayClass);` 获取数组第一个元素的偏移地址  
- `int arrayIndexScale(Class<?> var1);` 数组中一个元素占据的内存空间

`arrayBaseOffset`与`arrayIndexScale`配合使用，可定位数组中每个元素在内存中的位置 ，如：

```java
// int base = Unsafe.ARRAY_INT_BASE_OFFSET
int base = unsafe.arrayBaseOffset(int[].class);
// int scale = Unsafe.ARRAY_INT_INDEX_SCALE;
int scale = unsafe.arrayIndexScale(int[].class);

int[] arr = {1, 2, 3};
unsafe.putInt(arr, (long) (base + scale * 2), 100);

for (int i = 0; i < arr.length; i++) {
int v = unsafe.getInt(arr, (long) (base + scale * i));
// 输出： 1 2 100 
System.out.print(v + " ");
```

## 读写字段

- `putXxx(long address, typeValue)`

- `putXxx(Object obj, long offset, typeValue)`

- `putXxxVolatile(Object obj, long offset, typeValue)` 实现了volatile语义的加强版的 putXxx 方法

- `putOrderedXxx(Object obj, long offset, typeValue)` volatile 会在从主内存读取，同时写的时候会写到主内存，putOrderedXxx 读的语义跟volatile一样，但是写的时候只写工作内存，不写主内存，所以不保证立即对其他线程可见

  > 编译器会在每一个 volatile 写操作**前面**插入StoreStore内存屏障，在写操作**后面**插入StoreLoad内存屏障，StoreStore内存屏障可以在volatile写之前将前面的其他写刷新回主内存使其对其他处理器可见，StoreLoad内存屏障不但包含了StoreStore屏障的功能，并且还能禁止对Volatile变量的写和后面的Volatile变量读操作的重排序，从而不但能保证将本次volatile写入操作立即回写到主存使其对其他处理器可见，而且还能保证后面的读取操作立即重新从主存中加载以获取最新的值。从这两种屏障可以看出，StoreStore屏障只保证写入的顺序执行，并且只会将屏障之前的其他写刷新到主存使其对其他处理器可见，但是不会将本次的validate写刷新到主存，所以无法保证本次volatile的写入的立即可见性。而StoreLoad屏障就能满足所有的立即可见的要求，但是这也导致了StoreLoad屏障的巨大开销和性能的损耗。
  >
  > 我们可以把它们理解为：Java编译器会在 putOrderedXxx 方法相应的指令前面加上StoreStore指令，从而避免写入操作的重排序但是并不保证本次写入对其他线程立即可见，可能这也是方放名中携带有Ordered字样的体现吧
  >
  > ——[Java并发包基础元件sun.misc.Unsafe](https://pzh9527.iteye.com/blog/2415123)

- `putXxx(long address)`
- `getXxx(Object obj, long offset)`
- `getXxxVolatile(Object obj, long offset)` 实现了volatile语义的加强版的 getXxx 方法

>  参数 offset 通过 `objectFieldOffset(Field f)` 和 `staticFieldOffset(Field f)`  获取



## 内存管理

- `long allocateMemory(long bytes)` ： 分配指定的大小的内存，返回内存地址
- `void freeMemory(long address)`：释放一块内存，参数为内存地址
- `long reallocateMemory(long address, long bytes)` 根据给定的内存地址address，重新分配指定大小的内存


## 内存屏障

- `void loadFence();` ：该方法之前的所有 load操作（读） 在内存屏障之前完成
- `void storeFence();`：该方法之前的所有 store操作（写） 在内存屏障之前完成
- `void fullFence();` ：该方法之前的所有 load、store操作 在内存屏障之前完成

# 参考

- [Java并发包基础元件sun.misc.Unsafe](https://pzh9527.iteye.com/blog/2415123)
- [Java Magic. Part 4: sun.misc.Unsafe](http://ifeve.com/sun-misc-unsafe/)
- [Java Magic. Part 4: sun.misc.Unsafe](http://mishadoff.com/blog/java-magic-part-4-sun-dot-misc-dot-unsafe/)
