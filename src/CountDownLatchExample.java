import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// Simulates a race: all runners wait at the starting line,
// main fires the gun, then waits for everyone to finish.
public class CountDownLatchExample {

    public static void main(String[] args) throws InterruptedException {
        int numRunners = 5;
        CountDownLatch startGun = new CountDownLatch(1);       // 1 shot to start everyone
        CountDownLatch finishLine = new CountDownLatch(numRunners); // wait for all to finish

        ExecutorService executor = Executors.newFixedThreadPool(numRunners);

        for (int i = 0; i < numRunners; i++) {
            final int runnerId = i;
            executor.submit(() -> {
                try {
                    System.out.println("Runner " + runnerId + " is at the starting line.");
                    startGun.await(); // all runners block here until gun fires

                    System.out.println("Runner " + runnerId + " is running!");
                    Thread.sleep((long) (Math.random() * 2000));
                    System.out.println("Runner " + runnerId + " finished!");
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    finishLine.countDown();
                }
            });
        }

        Thread.sleep(5000); // let all runners reach the starting line
        System.out.println("\nAll runners ready. BANG!\n");
        startGun.countDown(); // fire — releases all runners simultaneously

        finishLine.await(); // main waits for all runners to finish
        System.out.println("\nAll runners finished. Race over.");

        executor.shutdown();
    }
}
