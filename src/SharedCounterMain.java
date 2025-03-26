import java.util.concurrent.*;
import java.util.*;

public class SharedCounterMain {
    private static final int NUM_TASKS = 10;
    private static final ExecutorService executor = Executors.newFixedThreadPool(4);
    private static final CompletionService<Integer> completionService = new ExecutorCompletionService<>(executor);
    public static void main(String[] args) {
        SharedCounter counter = new SharedCounter();
        List<Future<Integer>> futures = new ArrayList<>();

        for (int i = 0; i < NUM_TASKS; i++) {
            int taskId = i;
            futures.add(completionService.submit(() -> {
                Thread.sleep((long) (Math.random() * 5000)); // Simulate work
                counter.increment();
                return taskId;
            }));
        }

        for (int i = 0; i < NUM_TASKS; i++) {
            try {
                Future<Integer> future = completionService.take(); // Wait for the next completed task
                System.out.println("Task " + future.get() + " completed.");
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
  

        //executor.shutdown();
        System.out.println("Final Counter Value: " + counter.getCount());
    }
}
