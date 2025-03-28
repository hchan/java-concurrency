import java.util.UUID;
import java.util.concurrent.*;
import java.util.concurrent.locks.*;
import java.util.concurrent.atomic.*;

public class WebCrawler {
    public static int LIM = 1000;
    Semaphore semaphore = new Semaphore(LIM);
    StringBuffer sb = new StringBuffer();
    ReentrantLock lock = new ReentrantLock();

    public void process(String url)  {
        try {
            System.out.println("AVAILABLE PERMITS: " + semaphore.availablePermits());

            semaphore.acquire();
            System.out.println("url: " + url);
            lock.lock();
            sb.append(url);
            Thread.sleep((long) (Math.random() * 1000)); // Simulate processing time
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            lock.unlock();
            semaphore.release();
        }
    }
    public static void main(String[] args) throws InterruptedException {
        WebCrawler crawler = new WebCrawler();
        int workers = 1000;
        ExecutorService executor = Executors.newFixedThreadPool(workers);
        for (int i = 0; i < workers; i++) {
            executor.submit(() -> {
                while (true) {
                    crawler.process(UUID.randomUUID().toString());
                    Thread.sleep((long) (Math.random() * 500)); // Simulate some delay between requests
                }
                
            });
        }

        executor.shutdown();
        executor.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
    }
}
