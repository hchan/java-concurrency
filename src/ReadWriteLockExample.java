import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ReadWriteLockExample {

    private static int sharedData = 0;
    private static final ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();
    private static final long START = System.currentTimeMillis();

    static long elapsed() { return System.currentTimeMillis() - START; }

    static void read(int readerId) {
        rwLock.readLock().lock();
        try {
            System.out.printf("[%4dms] Reader %d START  (active readers: %d)%n",
                    elapsed(), readerId, rwLock.getReadLockCount());
            Thread.sleep(1000); // hold read lock for 1 second
            System.out.printf("[%4dms] Reader %d END%n", elapsed(), readerId);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            rwLock.readLock().unlock();
        }
    }

    static void write(int value) {
        System.out.printf("[%4dms] Writer   WAITING...%n", elapsed());
        rwLock.writeLock().lock();
        try {
            System.out.printf("[%4dms] Writer   START  (exclusive) %d -> %d%n",
                    elapsed(), sharedData, value);
            Thread.sleep(500);
            sharedData = value;
            System.out.printf("[%4dms] Writer   END%n", elapsed());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(5);

        // Readers 1, 2, 3 start immediately — should all overlap
        executor.submit(() -> read(1));
        executor.submit(() -> read(2));
        executor.submit(() -> read(3));

        // Writer submitted while readers are still active — must wait for all readers
        executor.submit(() -> write(42));

        // Delay ensures writer grabs the write lock before Reader 4 tries to read
        Thread.sleep(100);

        // Reader 4 submitted while writer holds lock — must wait for writer to finish
        executor.submit(() -> read(4));

        executor.shutdown();
    }
}
