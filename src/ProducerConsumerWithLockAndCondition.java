import java.util.concurrent.locks.*;
import java.util.LinkedList;
import java.util.Queue;

public class ProducerConsumerWithLockAndCondition {

    // Shared buffer
    private static final Queue<Integer> buffer = new LinkedList<>();
    private static final int MAX_CAPACITY = 10;  // Max capacity of buffer
    
    // Lock and Condition objects
    private static final Lock lock = new ReentrantLock();
    private static final Condition notFull = lock.newCondition();
    private static final Condition notEmpty = lock.newCondition();
    
    // Producer thread
    static class Producer implements Runnable {
        @Override
        public void run() {
            while (true) {
                try {
                    lock.lock();  // Acquire lock

                    // Wait until there's space in the buffer
                    while (buffer.size() == MAX_CAPACITY) {
                        notFull.await();
                    }

                    // Produce an item
                    int item = (int) (Math.random() * 100);
                    buffer.add(item);
                    System.out.println("Produced: " + item);

                    // Notify consumer that the buffer is not empty
                    notEmpty.signal();

                    // Sleep for a while to simulate work
                    Thread.sleep((long) (Math.random() * 1000));
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    lock.unlock();  // Release lock
                }
            }
        }
    }

    // Consumer thread
    static class Consumer implements Runnable {
        @Override
        public void run() {
            while (true) {
                try {
                    lock.lock();  // Acquire lock

                    // Wait until the buffer is not empty
                    while (buffer.isEmpty()) {
                        notEmpty.await();
                    }

                    // Consume an item
                    int item = buffer.poll();
                    System.out.println("Consumed: " + item);

                    // Notify producer that there's space in the buffer
                    notFull.signal();

                    // Sleep for a while to simulate work
                    Thread.sleep((long) (Math.random() * 1000));
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    lock.unlock();  // Release lock
                }
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        // Create and start producer and consumer threads
        Thread producerThread = new Thread(new Producer());
        Thread consumerThread = new Thread(new Consumer());

        producerThread.start();
        consumerThread.start();

        // Wait for threads to finish (infinite loop in this example)
        producerThread.join();
        consumerThread.join();
    }
}
