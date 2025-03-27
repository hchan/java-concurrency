import java.util.concurrent.*;
import java.util.concurrent.locks.*;
import java.util.concurrent.atomic.*;

public class DiningPhilosophersReentrantTryLock {
    final static int NUM_PHILOSOPHERS = 5;
    final static Philosopher[] philosophers = new Philosopher[NUM_PHILOSOPHERS];
    final static ReentrantLock[] chopsticks = new ReentrantLock[NUM_PHILOSOPHERS];

    public static void main(String[] args) {
        for (int i = 0; i < NUM_PHILOSOPHERS; i++) {
            chopsticks[i] = new ReentrantLock();
        }
        ExecutorService executor = Executors.newFixedThreadPool(NUM_PHILOSOPHERS);
        for (int i = 0; i < NUM_PHILOSOPHERS; i++) {
            // new Thread(new DiningPhilosophersReentrantTryLock.Philosopher(i)).start();
            executor.submit(new Philosopher(i));
        }
        executor.shutdown();
        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    static class Philosopher implements Runnable {

        int id;

        public Philosopher(int id) {
            this.id = id;
        }

        void eat() {
            System.out.println("Philosopher " + id + " is eating.");
            try {

                Thread.sleep((long) (Math.random() * 1));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        void pickupChopsticks() {
            System.out.println("Philosopher " + id + " attempting to picked up chopsticks.");
            int[] chopstickIds = getChopstickIds();
            ReentrantLock leftChopstick = DiningPhilosophersReentrantTryLock.chopsticks[chopstickIds[0]];
            ReentrantLock rightChopstick = DiningPhilosophersReentrantTryLock.chopsticks[chopstickIds[1]];
            try {

                if (rightChopstick.tryLock(100, TimeUnit.MILLISECONDS)) {
                    if (leftChopstick.tryLock(100, TimeUnit.MILLISECONDS)) {
                        System.out.println("Philosopher " + id + " picked up chopsticks.");
                    } else {
                        rightChopstick.unlock();
                        System.out.println("Philosopher " + id + " failed to pick up left chopstick.");
                        Thread.sleep((long) (Math.random() * 1000)); // backoff
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        int[] getChopstickIds() {
            int left = id;
            int right = (id + 1) % DiningPhilosophersReentrantTryLock.NUM_PHILOSOPHERS;
            int min = Math.min(left, right);
            int max = Math.max(left, right);
            // return new int[] { min, max };
            return new int[] { id, (id + 1) % DiningPhilosophersReentrantTryLock.NUM_PHILOSOPHERS };
        }

        void putdownChopsticks() {
            System.out.println("Philosopher " + id + " put down chopsticks.");
            ReentrantLock leftChopstick = DiningPhilosophersReentrantTryLock.chopsticks[id];
            leftChopstick.unlock();
            ReentrantLock rightChopstick = DiningPhilosophersReentrantTryLock.chopsticks[(id + 1)
                    % DiningPhilosophersReentrantTryLock.NUM_PHILOSOPHERS];
            rightChopstick.unlock();
        }

        public void run() {
            while (true) {

                pickupChopsticks();
                eat();
                putdownChopsticks();
            }
        }
    }

}