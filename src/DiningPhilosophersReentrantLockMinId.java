import java.util.concurrent.locks.ReentrantLock;

public class DiningPhilosophersReentrantLockMinId {
    final static int  NUM_PHILOSOPHERS = 5;
    final static Philosopher[] philosophers = new Philosopher[NUM_PHILOSOPHERS];
    final static ReentrantLock[] chopsticks = new ReentrantLock[NUM_PHILOSOPHERS];
    public static void main(String[] args) {
        for (int i = 0; i < NUM_PHILOSOPHERS; i++) {
            chopsticks[i] = new ReentrantLock();
        }
        for (int i = 0; i < NUM_PHILOSOPHERS; i++) {
            new Thread(new Philosopher(i)).start();
        }
    }
}

class Philosopher implements Runnable {

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
        ReentrantLock leftChopstick = DiningPhilosophersReentrantLockMinId.chopsticks[chopstickIds[0]];
        leftChopstick.lock();
        ReentrantLock rightChopstick = DiningPhilosophersReentrantLockMinId.chopsticks[chopstickIds[1]];
        rightChopstick.lock();
        System.out.println("Philosopher " + id + " picked up chopsticks.");
    }

    int[] getChopstickIds() {
        int left = id;
        int right = (id + 1) % DiningPhilosophersReentrantLockMinId.NUM_PHILOSOPHERS;
        int min = Math.min(left, right);
        int max = Math.max(left, right);
        return new int[] { min, max };
    }

    void putdownChopsticks() {
        System.out.println("Philosopher " + id + " put down chopsticks.");
        ReentrantLock leftChopstick = DiningPhilosophersReentrantLockMinId.chopsticks[id];
        leftChopstick.unlock();
        ReentrantLock rightChopstick = DiningPhilosophersReentrantLockMinId.chopsticks[(id + 1) % DiningPhilosophersReentrantLockMinId.NUM_PHILOSOPHERS];
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
   