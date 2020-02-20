package xyz.kail.demo.java.concurrent.locks.reentrant;

import xyz.kail.demo.java.concurrent.utils.ThreadUtil;
import xyz.kail.demo.java.concurrent.utils.Tool;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class ReentrantLockMain {


    public static void main(String[] args) throws InterruptedException {

        final ReentrantLock lock = new ReentrantLock(true);
        final Condition condition = lock.newCondition();

        final ArrayList<Integer> array = new ArrayList<>();
        for (int i = 0; i < 200; i++) {
            array.add(i);
        }

        final Runnable runnable = () -> {

            for (; ; ) {
                try {
                    lock.lock();
                    ThreadUtil.sleep(50, TimeUnit.MILLISECONDS);

                    condition.signal();
                    condition.signalAll();

                    if (array.isEmpty()) {
                        break;
                    }

                    Tool.println(array.remove(0));

                    try {
                        condition.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } finally {
                    lock.unlock();
                }
            }

        };

        new Thread(runnable).start();
        new Thread(runnable).start();
        new Thread(runnable).start();
        new Thread(runnable).start();

    }

}
