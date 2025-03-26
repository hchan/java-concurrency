import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReentrantLock;

public class DiningPhilosophersSemaphore {
    private static final int NUM_PHILOSOPHERS = 5;
    private final ReentrantLock[] chopsticks = new ReentrantLock[NUM_PHILOSOPHERS];
    private final Semaphore waiter = new Semaphore(NUM_PHILOSOPHERS-1); // Limit 4 philosophers at a time

    public DiningPhilosophersSemaphore() {
        for (int i = 0; i < NUM_PHILOSOPHERS; i++) {
            chopsticks[i] = new ReentrantLock();
        }
    }

    public static void main(String[] args) {
        DiningPhilosophersSemaphore dp = new DiningPhilosophersSemaphore();
        for (int i = 0; i < NUM_PHILOSOPHERS; i++) {
            new Thread(dp.new Philosopher(i)).start();
        }
    }

    class Philosopher implements Runnable {
        private final int id;
        private final ReentrantLock leftChopstick;
        private final ReentrantLock rightChopstick;

        public Philosopher(int id) {
            this.id = id;
            leftChopstick = chopsticks[id];
            rightChopstick = chopsticks[(id + 1) % NUM_PHILOSOPHERS];
        }

        @Override
        public void run() {
            try {
                while (true) {
                    think();
                    pickUpChopsticks();
                    eat();
                    putDownChopsticks();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        private void think() throws InterruptedException {
            System.out.println("Philosopher " + id + " is thinking.");
            Thread.sleep((long) (Math.random() * 1));
        }

        private void pickUpChopsticks() throws InterruptedException {
            waiter.acquire(); // Request permission to eat (max 4 philosophers at a time)

            leftChopstick.lock();
            System.out.println("Philosopher " + id + " picked up left chopstick.");

            rightChopstick.lock();
            System.out.println("Philosopher " + id + " picked up right chopstick.");
        }

        private void eat() throws InterruptedException {
            System.out.println("Philosopher " + id + " is eating.");
            Thread.sleep((long) (Math.random() * 1000));
        }

        private void putDownChopsticks() {
            rightChopstick.unlock();
            System.out.println("Philosopher " + id + " released right chopstick.");

            leftChopstick.unlock();
            System.out.println("Philosopher " + id + " released left chopstick.");

            waiter.release(); // Allow another philosopher to eat
        }
    }
}
