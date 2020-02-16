package xyz.kail.demo.java.concurrent.thread;

import java.util.Map;

public class AllStackTracesThreadMain {

    /**
     * Thread Name::: Reference Handler
     * java.lang.Object.wait(Native Method)
     * java.lang.Object.wait(Object.java:502)
     * java.lang.ref.Reference.tryHandlePending(Reference.java:191)
     * java.lang.ref.Reference$ReferenceHandler.run(Reference.java:153)
     *
     * Thread Name::: Monitor Ctrl-Break
     * java.net.PlainSocketImpl.socketCreate(Native Method)
     * java.net.AbstractPlainSocketImpl.create(AbstractPlainSocketImpl.java:109)
     * java.net.Socket.createImpl(Socket.java:457)
     * java.net.Socket.<init>(Socket.java:431)
     * java.net.Socket.<init>(Socket.java:211)
     * com.intellij.rt.execution.application.AppMainV2$1.run(AppMainV2.java:59)
     *
     * Thread Name::: Signal Dispatcher
     *
     * Thread Name::: Finalizer
     * java.lang.Object.wait(Native Method)
     * java.lang.ref.ReferenceQueue.remove(ReferenceQueue.java:143)
     * java.lang.ref.ReferenceQueue.remove(ReferenceQueue.java:164)
     * java.lang.ref.Finalizer$FinalizerThread.run(Finalizer.java:209)
     *
     * Thread Name::: main
     * java.lang.Thread.dumpThreads(Native Method)
     * java.lang.Thread.getAllStackTraces(Thread.java:1610)
     * xyz.kail.demo.java.concurrent.thread.AllStackTracesThreadMain.main(AllStackTracesThreadMain.java:8)
     */
    public static void main(String[] args) {
        final Map<Thread, StackTraceElement[]> allStackTraces = Thread.getAllStackTraces();
        allStackTraces.forEach((thread, value) -> {
            System.out.println("Thread Name::: " + thread.getName());
            StackTraceThreadMain.printStackTrace(value);
            System.out.println();

        });
    }
}
