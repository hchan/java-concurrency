import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CountDownLatchExample {

    public static void main(String[] args) throws InterruptedException {
        int numWorkers = 5;
        CountDownLatch latch = new CountDownLatch(numWorkers);
        ExecutorService executor = Executors.newFixedThreadPool(numWorkers);

        for (int i = 0; i < numWorkers; i++) {
            final int workerId = i;
            executor.submit(() -> {
                try {
                    System.out.println("Worker " + workerId + " started.");
                    Thread.sleep((long) (Math.random() * 2000));
                    System.out.println("Worker " + workerId + " done.");
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    latch.countDown(); // always decrement, even on failure
                }
            });
        }

        System.out.println("Main thread waiting for all workers...");
        latch.await(); // blocks until count reaches 0
        System.out.println("All workers done. Proceeding.");

        executor.shutdown();
    }
}
