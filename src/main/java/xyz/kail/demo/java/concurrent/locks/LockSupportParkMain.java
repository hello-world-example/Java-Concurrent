package xyz.kail.demo.java.concurrent.locks;

import java.util.HashMap;
import java.util.concurrent.locks.LockSupport;

public class LockSupportParkMain {

    public static void main(String[] args) {
/**
 * "main" #1 prio=5 os_prio=31 tid=0x00007fb4e6802000 nid=0x1103 waiting on condition [0x0000700009ca2000]
 *    java.lang.Thread.State: WAITING (parking)
 *         at sun.misc.Unsafe.park(Native Method)
 *         at java.util.concurrent.locks.LockSupport.park(LockSupport.java:304)
 *         at xyz.kail.demo.java.concurrent.locks.LockSupportParkMain.main(LockSupportParkMain.java:8)
 */
// LockSupport.park();

/**
 * "main" #1 prio=5 os_prio=31 tid=0x00007f7fc8002000 nid=0xd03 waiting on condition [0x0000700001b1d000]
 *    java.lang.Thread.State: WAITING (parking)
 *         at sun.misc.Unsafe.park(Native Method)
 *         - parking to wait for  <0x000000076ada4748> (a java.util.HashMap)
 *         at java.util.concurrent.locks.LockSupport.park(LockSupport.java:175)
 *         at xyz.kail.demo.java.concurrent.locks.LockSupportParkMain.main(LockSupportParkMain.java:18)
 */
        LockSupport.park(new HashMap<>());
    }

}
