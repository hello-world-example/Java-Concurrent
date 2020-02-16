package xyz.kail.demo.java.concurrent.thread;

public class StackTraceThreadMain {

    public static void printStackTrace(StackTraceElement[] stackTraces) {
        for (StackTraceElement stackTrace : stackTraces) {
            System.out.println(stackTrace.toString());
        }
    }

    private static void b() {
        printStackTrace(Thread.currentThread().getStackTrace());
    }


    private static void a() {
        b();
    }

    /**
     * java.lang.Thread.getStackTrace(Thread.java:1559)
     * xyz.kail.demo.java.concurrent.thread.StackTraceThreadMain.b(StackTraceThreadMain.java:12)
     * xyz.kail.demo.java.concurrent.thread.StackTraceThreadMain.a(StackTraceThreadMain.java:17)
     * xyz.kail.demo.java.concurrent.thread.StackTraceThreadMain.main(StackTraceThreadMain.java:30)
     */
    public static void main(String[] args) {
        a();
    }

}
