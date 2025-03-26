import java.util.concurrent.locks.ReentrantLock;

public class DiningPhilosophersReentrantLock {
    private static final int NUM_PHILOSOPHERS = 5;
    private final ReentrantLock[] chopsticks = new ReentrantLock[NUM_PHILOSOPHERS];
    
    public DiningPhilosophersReentrantLock() {
        // Initialize each chopstick as a ReentrantLock
        for (int i = 0; i < NUM_PHILOSOPHERS; i++) {
            chopsticks[i] = new ReentrantLock();
        }
    }
    
    public static void main(String[] args) {
        DiningPhilosophersReentrantLock dp = new DiningPhilosophersReentrantLock();
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
            // Each philosopher’s left chopstick is at position "id"
            // and right chopstick is at position "(id + 1) % NUM_PHILOSOPHERS"
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
                return;
            }
        }
        
        private void think() throws InterruptedException {
            System.out.println("Philosopher " + id + " is thinking.");
            Thread.sleep((long) (Math.random() * 1000));
        }
        
        private void pickUpChopsticks() {
            // Partial order: pick up the lower-numbered chopstick first.
            // For all philosophers except the last one, left is lower than right.
            // The last philosopher reverses the order to break the cycle.
            if (id != NUM_PHILOSOPHERS - 1) {
                leftChopstick.lock();
                System.out.println("Philosopher " + id + " picked up left chopstick.");
                rightChopstick.lock();
                System.out.println("Philosopher " + id + " picked up right chopstick.");
            } else {
                rightChopstick.lock();
                System.out.println("Philosopher " + id + " picked up right chopstick.");
                leftChopstick.lock();
                System.out.println("Philosopher " + id + " picked up left chopstick.");
            }
        }
        
        private void eat() throws InterruptedException {
            System.out.println("Philosopher " + id + " is eating.");
            Thread.sleep((long) (Math.random() * 1000));
        }
        
        private void putDownChopsticks() {
            // Release order does not impact deadlock prevention.
            // Here, we release in the reverse order of acquisition for clarity.
            if (id != NUM_PHILOSOPHERS - 1) {
                rightChopstick.unlock();
                System.out.println("Philosopher " + id + " released right chopstick.");
                leftChopstick.unlock();
                System.out.println("Philosopher " + id + " released left chopstick.");
            } else {
                leftChopstick.unlock();
                System.out.println("Philosopher " + id + " released left chopstick.");
                rightChopstick.unlock();
                System.out.println("Philosopher " + id + " released right chopstick.");
            }
        }
    }
}
