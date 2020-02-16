package xyz.kail.demo.java.concurrent.thread;

import lombok.SneakyThrows;

public class WatNotifyThreadMain {

    private static String packet;

    // True if receiver should wait
    // False if sender should wait
    private static boolean transfer = true;

    @SneakyThrows
    public static synchronized void send(String packet){
        while (!transfer) {
            try {
                WatNotifyThreadMain.class.wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();

            }
        }
        transfer = false;


        WatNotifyThreadMain.packet = packet;
        System.out.println("send:" + packet);

        WatNotifyThreadMain.class.notifyAll();
    }

    @SneakyThrows
    public static synchronized String receive() {
        while (transfer) {
            try {
                WatNotifyThreadMain.class.wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        transfer = true;
        System.out.println("receive:" + packet);

        WatNotifyThreadMain.class.notifyAll();
        return packet;
    }

    public static void main(String[] args) {

        new Thread(() -> {
            for (int i = 0; i < 100; i++) {
                WatNotifyThreadMain.send("" + i);
            }
        }).start();

        new Thread(() -> {
            for (; ; ) {
                WatNotifyThreadMain.receive();
            }
        }).start();

    }

}
