import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class SharedCounter {
    private int count = 0;
    private final Object lock = new Object();
private final AtomicInteger counter = new AtomicInteger(0);
    public void increment() {
        //synchronized (lock) {
        //    count+=2;
        //}
        counter.updateAndGet(i -> i + 3);
    }

    public int getCount() {
        //synchronized (lock) {
        //    return count;
        //}
        return counter.get();
    }
}
