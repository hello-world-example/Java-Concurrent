package xyz.kail.demo.java.concurrent.queue;

import java.util.concurrent.PriorityBlockingQueue;

public class PriorityBlockingQueueMain {

    public static void main(String[] args) throws InterruptedException {
        PriorityBlockingQueue blockingQueue = new PriorityBlockingQueue();

        blockingQueue.put(new Object());
        blockingQueue.take();

    }

}
