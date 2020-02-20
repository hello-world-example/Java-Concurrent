package xyz.kail.demo.java.concurrent.utils;

public class Tool {


    public static long currentSeconds() {
        return System.currentTimeMillis() / 1000 % 1000;
    }

    public static void println(Object obj) {
        System.out.println(currentSeconds() + " : " + Thread.currentThread().getName() + " : " + obj);
    }

}
