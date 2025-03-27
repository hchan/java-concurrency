import java.util.concurrent.*;
import java.util.concurrent.locks.*;
import java.util.concurrent.atomic.*;

public class LogProcessor {
    
    // Semaphores to enforce ordering: A (first) starts immediately.
    private final Semaphore semA = new Semaphore(1);
    private final Semaphore semB = new Semaphore(0);
    private final Semaphore semC = new Semaphore(0);

    /**
     * Processes the log from subsystem A.
     * @param printFirst Runnable that prints the log line for subsystem A.
     */
    public void printFirst(Runnable printFirst) {
        try {
            semA.acquire(); // Ensure it runs first in the cycle.
            // Robustness: Retry until successful.
            while (true) {
                try {
                    printFirst.run();
                    break; // Exit loop when successful.
                } catch (Exception e) {
                    // Log error, wait briefly, then retry.
                    System.err.println("Subsystem A failed, retrying...");
                    Thread.sleep(100); // Retry delay; adjust as needed.
                }
            }
            semB.release(); // Allow subsystem B to proceed.
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Processes the log from subsystem B.
     * @param printSecond Runnable that prints the log line for subsystem B.
     */
    public void printSecond(Runnable printSecond) {
        try {
            semB.acquire(); // Wait for subsystem A to complete.
            // Retry loop for robustness.
            while (true) {
                try {
                    printSecond.run();
                    break;
                } catch (Exception e) {
                    System.err.println("Subsystem B failed, retrying...");
                    Thread.sleep(100);
                }
            }
            semC.release(); // Signal that subsystem C can run.
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Processes the log from subsystem C.
     * @param printThird Runnable that prints the log line for subsystem C.
     */
    public void printThird(Runnable printThird) {
        try {
            semC.acquire(); // Wait for subsystem B to complete.
            // Retry loop for robustness.
            while (true) {
                try {
                    printThird.run();
                    break;
                } catch (Exception e) {
                    System.err.println("Subsystem C failed, retrying...");
                    Thread.sleep(100);
                }
            }
            semA.release(); // Cycle complete; allow subsystem A to start next cycle.
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    // For demonstration, simulate concurrent calls.
    public static void main(String[] args) {
        LogProcessor processor = new LogProcessor();

        // Simulated subsystem log printing functions.
        Runnable printA = () -> { 
            System.out.println("Log from subsystem A");
            try {
                // Simulate some processing time.
                Thread.sleep((long)(Math.random() * 1000));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        };
        Runnable printB = () -> {
            System.out.println("Log from subsystem B");
            try {
                // Simulate some processing time.
                Thread.sleep((long)(Math.random() * 1000));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        };
        Runnable printC = () -> {
            System.out.println("Log from subsystem C");
            try {
                // Simulate some processing time.
                Thread.sleep((long)(Math.random() * 1000));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        };
        ExecutorService executor = Executors.newFixedThreadPool(3);
        // In a real system, these might be called concurrently in each cycle.
        // For demo purposes, we simulate 3 cycles.
        for (int i = 0; i < 10; i++) {
            executor.submit(
                () -> processor.printFirst(printA)
            );
            executor.submit(
                () -> processor.printSecond(printB)
            );
            executor.submit(
                () -> processor.printThird(printC)
            );
            
        }
        System.out.println("Shutting down executor...");
        executor.shutdown();
        
        try {
            executor.awaitTermination(1, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("All tasks completed.");
    }
}