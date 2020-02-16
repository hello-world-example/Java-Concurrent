package xyz.kail.demo.java.concurrent.locks;

import java.util.HashMap;
import java.util.concurrent.locks.LockSupport;

public class LockSupportThreadMain {

    public static void main(String[] args) {
        final Thread threadMain = Thread.currentThread();

        new Thread(() -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
            }
            System.out.println("unpark" + System.currentTimeMillis());
            System.out.println(LockSupport.getBlocker(threadMain));
            LockSupport.unpark(threadMain);
        }).start();

        System.out.println("park  " + System.currentTimeMillis());
        LockSupport.park(new HashMap<>());
        System.out.println("ok    " + System.currentTimeMillis());

    }

}
