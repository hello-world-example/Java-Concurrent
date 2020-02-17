package xyz.kail.demo.java.concurrent.futuretask;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

public class FutureTaskMain {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        FutureTask<String> futureTask = new FutureTask<>(() -> {
            Thread.sleep(3000);
            return "ok";
        });


        new Thread(new Runnable() {
            @Override
            public void run() {
                futureTask.run();
            }
        }).start();

        final String result = futureTask.get();

        System.out.println(result);
    }

}
