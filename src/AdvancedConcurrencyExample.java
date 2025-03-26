import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class AdvancedConcurrencyExample {
    private static final AtomicInteger counter = new AtomicInteger(0);
    private static final Semaphore semaphore = new Semaphore(3);  // Limit access to 3 threads

    public static void main(String[] args) throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(5);

        for (int i = 0; i < 5; i++) {
            executorService.submit(() -> {
                try {
                    semaphore.acquire();  // Acquire a permit before proceeding
                    System.out.println("Thread " + Thread.currentThread().getName() + " started.");
                    
                    // Simulate some work
                    TimeUnit.SECONDS.sleep(2);

                    // Increment the counter atomically
                    int newCount = counter.incrementAndGet();
                    System.out.println("Thread " + Thread.currentThread().getName() + " incremented count to " + newCount);

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    semaphore.release();  // Release the permit
                }
            });
        }

        executorService.shutdown();
        executorService.awaitTermination(1, TimeUnit.MINUTES);
    }
}
