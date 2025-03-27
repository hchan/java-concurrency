import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
public class ExecutorExample {

    public static void main(String[] args) {
        ExecutorService executor = Executors.newFixedThreadPool(2);
        List<Future<Integer>> futures = new ArrayList<>();
       
        for (int i = 0; i < 5; i++) {
            final int taskId = i;
            futures.add(executor.submit( 
                () -> {
                    try {
                        Thread.sleep((long)(Math.random() * 1000));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    return taskId;
                }
            ));
        }
        // Process the results using Future objects
        for (Future<Integer> future : futures) {
            try {
                // Get the result of each task (this will block until the task completes)
                Integer result = future.get();
                System.out.println("Task result: " + result);
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        executor.shutdown();
    }
}
