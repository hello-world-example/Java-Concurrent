package xyz.kail.demo.java.concurrent.utils;

import lombok.SneakyThrows;

import java.util.concurrent.TimeUnit;

public class ThreadUtil {

    @SneakyThrows
    public static void sleep(long timeout){
        Thread.sleep(timeout);
    }

    @SneakyThrows
    public static void sleep(long timeout, TimeUnit unit){
        unit.sleep(timeout);
    }



}
