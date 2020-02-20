package xyz.kail.demo.java.concurrent.queue;

import xyz.kail.demo.java.concurrent.utils.ThreadUtil;
import xyz.kail.demo.java.concurrent.utils.Tool;

import java.util.Random;
import java.util.concurrent.Exchanger;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ExchangerMain {

    public static void main(String[] args) {
        Exchanger<Integer> exchanger = new Exchanger<>();

        Runnable runnable = () -> {
            for (; ; ) {
                final int newData = Math.abs(new Random().nextInt() % 100);
                Integer oldData = 0;
                try {
                    Tool.println("给你 → " + newData);
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


    }

}
