public class VolatileExample {

    private static volatile boolean runningVolatile = true;

    private static boolean runningLock = true;
    private static final Object lock = new Object();

    public static void main(String[] args) throws InterruptedException {

        // --- volatile version ---
        Thread volatileWorker = new Thread(() -> {
            System.out.println("Volatile worker started.");
            while (runningVolatile) {}
            System.out.println("Volatile worker stopped.");
        });

        // --- lock version ---
        Thread lockWorker = new Thread(() -> {
            System.out.println("Lock worker started.");
            while (true) {
                synchronized (lock) {
                    if (!runningLock) break;
                }
            }
            System.out.println("Lock worker stopped.");
        });

        volatileWorker.start();
        lockWorker.start();
        Thread.sleep(500);

        System.out.println("Main: stopping both workers...");

        runningVolatile = false;

        synchronized (lock) {
            runningLock = false;
        }

        volatileWorker.join();
        lockWorker.join();
        System.out.println("Done.");
    }
}
