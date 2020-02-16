package xyz.kail.demo.java.concurrent.locks;

import java.util.concurrent.locks.LockSupport;

public class LockSupportUnpark2Main {

    public static void main(String[] args) {
        LockSupport.unpark(Thread.currentThread());
        LockSupport.unpark(Thread.currentThread());
        System.out.println("这里直接通过");
        LockSupport.park();
        System.out.println("这里仍然会阻塞");
        LockSupport.park();
        System.out.println("end");
    }

}
