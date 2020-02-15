package xyz.kail.demo.java.concurrent.queue;

import java.util.concurrent.SynchronousQueue;

public class SynchronousQueueMain {

    public static void main(String[] args) throws InterruptedException {
        SynchronousQueue<Integer> synchronousQueue = new SynchronousQueue<>();
        synchronousQueue.put(1);
        synchronousQueue.take();


    }

}
