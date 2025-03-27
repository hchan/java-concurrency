import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class CompletionServiceExample {

    public static void main(String[] args) {
        ExecutorService executor = Executors.newFixedThreadPool(2);
        CompletionService<Integer> completionService = new ExecutorCompletionService<>(executor);

        // Submit tasks to the CompletionService
        for (int i = 0; i < 5; i++) {
            final int taskId = i;
            completionService.submit(() -> {
                try {
                    // Simulate some work by sleeping for a random time
                    Thread.sleep((long)(Math.random() * 1000)); // Sleep between 0 and 10 seconds
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return taskId;
            });
        }

        // Process the results as they complete
        for (int i = 0; i < 5; i++) {
            try {
                // Take the result of the next completed task (this will block until one is available)
                Future<Integer> future = completionService.take();
                Integer result = future.get();
                System.out.println("Task result: " + result);
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

        executor.shutdown();
    }
}
