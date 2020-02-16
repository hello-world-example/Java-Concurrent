package xyz.kail.demo.java.concurrent.thread;

public class UncaughtExceptionThreadMain {

    public static void main(String[] args) {
        // 线程全局异常 处理器
        Thread.setDefaultUncaughtExceptionHandler((t, e) -> System.out.println("[System]线程 " + t.getName() + " 抛出异常" + e.getMessage()));

        final Thread thread = new Thread(() -> System.out.println(1 / 0));

        // 实例异常捕捉器
        thread.setUncaughtExceptionHandler((t, e) -> System.out.println("[Thread]线程 " + t.getName() + " 抛出异常" + e.getMessage()));

        thread.start();

    }

}
