package xyz.kail.demo.java.concurrent.locks;

import java.util.concurrent.locks.LockSupport;

public class LockSupportUnparkMain {

    public static void main(String[] args) {
        LockSupport.unpark(Thread.currentThread());
        System.out.println("这里直接通过");
        LockSupport.park();
        System.out.println("end");
    }

}
