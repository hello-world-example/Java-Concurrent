package xyz.kail.demo.java.concurrent.queue;

import java.util.concurrent.LinkedBlockingQueue;

public class LinkedBlockingQueueMain {

    public static void main(String[] args) throws InterruptedException {
        LinkedBlockingQueue blockingQueue = new LinkedBlockingQueue(10);

        blockingQueue.put(1);
        blockingQueue.take();

    }

}
