import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class ChainedFuturesExample {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        // First task: Adds two numbers
        CompletableFuture<Integer> future1 = CompletableFuture.supplyAsync(() -> {
            System.out.println("Task 1: Adding numbers...");
            return 10 + 20;
        });

        // Second task: Multiplies the result of the first task by 2
        CompletableFuture<Integer> future2 = future1.thenApplyAsync(result -> {
            System.out.println("Task 2: Multiplying by 2...");
            return result * 2;
        });

        // Third task: Subtracts 5 from the result of the second task
        CompletableFuture<Integer> future3 = future2.thenApplyAsync(result -> {
            System.out.println("Task 3: Subtracting 5...");
            return result - 5;
        });

        // Fourth task: Prints the final result
        CompletableFuture<Void> finalTask = future3.thenAcceptAsync(result -> {
            System.out.println("Final result: " + result);
        });

        // Wait for all tasks to complete
        finalTask.join();
    }
}
