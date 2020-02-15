package xyz.kail.demo.java.concurrent.action;

import java.util.concurrent.TimeUnit;

public class DataLoader {

    public final void load() {
        long startTime = System.currentTimeMillis(); // 开始时间
        doLoad(); // 具体执行
        long costTime = System.currentTimeMillis() - startTime; // 消耗时间
        System.out.println("load() 总耗时：" + costTime + " 毫秒");
    }

    protected void doLoad() { // 串行计算
        loadConfigurations();    //  耗时 1s
        loadUsers();                  //  耗时 2s
        loadOrders();                // 耗时 3s
    } // 总耗时 1s + 2s  + 3s  = 6s

    protected final void loadConfigurations() {
        loadMock("loadConfigurations()", 1);
    }

    protected final void loadUsers() {
        loadMock("loadUsers()", 2);
    }

    protected final void loadOrders() {
        loadMock("loadOrders()", 3);
    }

    private void loadMock(String source, int seconds) {
        try {
            long startTime = System.currentTimeMillis();
            TimeUnit.SECONDS.sleep(seconds);
            long costTime = System.currentTimeMillis() - startTime;

            System.out.printf("[线程 : %s] %s 耗时 :  %d 毫秒\n", Thread.currentThread().getName(), source, costTime);

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * [线程 : main] loadConfigurations() 耗时 :  1005 毫秒
     * [线程 : main] loadUsers() 耗时 :  2003 毫秒
     * [线程 : main] loadOrders() 耗时 :  3005 毫秒
     * load() 总耗时：6030 毫秒
     */
    public static void main(String[] args) {
        new DataLoader().load();
    }

}
