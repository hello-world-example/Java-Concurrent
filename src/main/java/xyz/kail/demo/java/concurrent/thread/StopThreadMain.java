package xyz.kail.demo.java.concurrent.thread;

import lombok.SneakyThrows;

import javax.sound.midi.SysexMessage;
import java.util.concurrent.TimeUnit;

public class StopThreadMain implements Runnable {

    @SneakyThrows
    @Override
    public void run() {
        System.out.println(1);
        TimeUnit.MILLISECONDS.sleep(100);
        System.out.println(2);
        TimeUnit.MILLISECONDS.sleep(100);
        System.out.println(3);
        TimeUnit.MILLISECONDS.sleep(100);
        System.out.println(4);
        TimeUnit.MILLISECONDS.sleep(100);
        System.out.println(5);
        TimeUnit.MILLISECONDS.sleep(100);
    }

    public static void main(String[] args) throws InterruptedException {
        final Thread thread = new Thread(new StopThreadMain());
        thread.start();
        TimeUnit.MILLISECONDS.sleep(300);
        thread.stop();
    }

}
