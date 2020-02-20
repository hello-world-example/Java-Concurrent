# LinkedBlockingQueue

> @see [ArrayBlockingQueue](../ArrayBlockingQueue)

- `LinkedBlockingQueue` 是 [BlockingQueue](../BlockingQueue) 的一个**基于链表**的实现
- 基于 `ReentrantLock` 的 `await` 和 `signal` 通知机制 实现阻塞，一个区别是
  - LinkedBlockingQueue 的增删操作分辨使用 两个锁，增删互不影响
  - ArrayBlockingQueue 使用一个锁，**增删的时候有竞态条件**
- **阻塞原理**几乎与  [ArrayBlockingQueue](../ArrayBlockingQueue) 一样，主要关注边界条件
- 入队和出队时的操作一个**链表节点的增删**，一个是**读写下标的平移**

