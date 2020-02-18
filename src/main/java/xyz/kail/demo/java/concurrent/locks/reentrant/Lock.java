package xyz.kail.demo.java.concurrent.locks.reentrant;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Lock {

    public static void main(String[] args) {

        ReentrantLock reentrantLock = new ReentrantLock();
        reentrantLock.lock();

        final Condition condition = reentrantLock.newCondition();
        condition.signalAll();

        final ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();
        final ReentrantReadWriteLock.ReadLock readLock = readWriteLock.readLock();
        readLock.lock();
        readLock.unlock();
        readLock.newCondition();

        final ReentrantReadWriteLock.WriteLock writeLock = readWriteLock.writeLock();
        writeLock.lock();
        writeLock.unlock();
        writeLock.newCondition();


    }

}
