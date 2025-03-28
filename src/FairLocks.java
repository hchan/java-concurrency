import java.util.concurrent.*;
import java.util.concurrent.locks.*;
import java.util.concurrent.atomic.*;

public class FairLocks {
    public static void main(String[] args) {
        ExecutorService executor = Executors.newFixedThreadPool(10);
        ReentrantLock lock1 = new ReentrantLock(true); // Fair lock
        ReentrantLock lock2 = new ReentrantLock(true); // Fair lock

        for (int i = 0; i < 100; i++) {
            executor.submit(() -> {
                
                try {
                    lock1.lock();
                    System.out.println("Thread 1 acquired lock 1");
                    Thread.sleep(1000);
                    lock2.lock();
                    System.out.println("Thread 1 acquired lock 2");
                    lock2.unlock();
                    lock1.unlock();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                
            });
            executor.submit(() -> {
                try {
                    lock1.lock();
                    System.out.println("Thread 1 acquired lock 2");
                    Thread.sleep(1000);
                    lock2.lock();
                    System.out.println("Thread 2 acquired lock 1");
                    lock2.unlock();
                    lock1.unlock();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
        }
        executor.shutdown();
        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
