//package xyz.kail.demo.java.concurrent.guava;
//
//import com.google.common.util.concurrent.Futures;
//import com.google.common.util.concurrent.ListenableFuture;
//import com.google.common.util.concurrent.ListenableFutureTask;
//
//import java.util.concurrent.*;
//
//public class Main {
//
//    public static void main(String[] args) throws ExecutionException, InterruptedException {
//        final ExecutorService executorService = Executors.newCachedThreadPool();
//
//        final ListenableFutureTask<String> futureTask = ListenableFutureTask.create(() -> {
//            return "2";
//        });
//
//        executorService.execute(futureTask);
//
//
//        futureTask.addListener(() -> {
//                    try {
//                        System.out.println(futureTask.get());
//                        System.out.println(futureTask.get());
//                        System.out.println(futureTask.get());
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//
//                    System.out.println("sucess");
//                }
//                , executorService
//        );
//
//        System.out.println("executorService.shutdown()");
//        executorService.shutdown();
//
//
//    }
//
//}
