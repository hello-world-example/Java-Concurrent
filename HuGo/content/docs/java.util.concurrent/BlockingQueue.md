# BlockingQueue



`BlockingQueue` 不接受 `null` ，试图 `add`、`put` 或 `offer` 一个 `null` 元素时，会抛出 `NullPointerException`，**`null` 被用作指示 `poll` 操作失败的警戒值**。



BlockingQueue 四种形的操作

- 在操作可以成功前，无限期地阻塞当前线程
- 抛出一个异常
- 返回一个特殊值（`null` 或 `false`，具体取决于操作）
- 阻塞指定的时间



|  | 阻塞 | 抛出异常 | 特殊值 | 超时 |
| :------: | :----: | :--: | :--: | :--: |
|插入	|`put`	|`add(e)` `IllegalArgumentException`| `offer(e)` |	`offer(e,time,unit)`	|
|移除	|`take`	|`remove`  `NoSuchElementException`	|`poll()`	|`poll(time, unit)`	|
|检查<br />获取但不移除	|-	|`element`  <br />  `NoSuchElementException`	|`peek`	|-	|



## 阻塞队列的实现

- [ArrayBlockingQueue](../ArrayBlockingQueue) 基于**数组**的 阻塞队列
- [LinkedBlockingQueue](../LinkedBlockingQueue) 基于**链表**的 阻塞对联 
- [SynchronousQueue](../SynchronousQueue) **没有长度** 的阻塞队列
- [DelayQueue](../DelayQueue) **延时**队列
- [PriorityBlockingQueue](../PriorityBlockingQueue) 有**权重**的 阻塞队列