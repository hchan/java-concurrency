class SynchronizedSimulationgLock {
    private boolean isLocked = false;

    public synchronized void lock() {
        while (isLocked) { 
            try {
                wait(); // Release the lock and wait
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // Restore interrupt status
            }
        }
        isLocked = true;
    }

    public synchronized void unlock() {
        isLocked = false;
        notify(); // Notify waiting threads
    }

    public synchronized void methodA() {
        System.out.println("Method A: Acquiring lock...");
        lock(); 
        System.out.println("Method A: Executing...");
        unlock();
    }

    public synchronized void methodB() {
        methodA(); // Still deadlocks if called by the same thread
    }

    public static void main(String[] args) {
        SynchronizedSimulationgLock obj = new SynchronizedSimulationgLock();
        obj.methodB(); // Still problematic for re-entrant scenarios
    }
}
