package xyz.kail.demo.java.concurrent.queue;

import xyz.kail.demo.java.concurrent.utils.ThreadUtil;
import xyz.kail.demo.java.concurrent.utils.Tool;

import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.SynchronousQueue;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class SynchronousQueueMain {

    public static void main(String[] args) throws InterruptedException {
        SynchronousQueue<Integer> sync = new SynchronousQueue<>();

        final int[] someData = new int[1];
        Consumer<Integer> changeIt = (newData) -> someData[0] = newData;
        Supplier<Integer> getChange = () -> someData[0];

        new Thread(() -> {
            for (; ; ) {
                final int newData = Math.abs(new Random().nextInt() % 100);
                changeIt.accept(newData);
                try {
                    Tool.println("我变了 " + newData);
                    sync.put(newData);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                ThreadUtil.sleep(newData * 100 / 2);

                if (newData < 30) {
                    break;
                }
            }
        }).start();

        new Thread(() -> {
            for (; ; ) {
                try {
                    final Integer data = sync.take();
                    Tool.println("知道了 " + getChange.get());
                    if (data < 30) {
                        break;
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();


    }

}
