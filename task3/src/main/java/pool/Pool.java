package pool;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.IntStream;


public class Pool {
    private static final Logger log = LoggerFactory.getLogger(Pool.class);
    LinkedList<Runnable> queue = new LinkedList<>();
    LinkedList<Thread> pool = new LinkedList<>();
    ReentrantLock mu = new ReentrantLock();
    Condition notEmpty = mu.newCondition();

    public Pool(Integer size) {
        IntStream.range(0, size).forEach(_ -> {
            Thread t = new Thread(() -> {
                while (!Thread.currentThread().isInterrupted()) {
                    Runnable f;
                    try {
                        this.mu.lockInterruptibly();
                        try {
                            while ((f = this.queue.poll()) == null) {
                                this.notEmpty.await();
                            }
                        } finally {
                            this.mu.unlock();
                        }
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        break;
                    }

                    try {
                        f.run();
                    } catch (Exception e) {
                        log.error("Task failed: {}", f, e);
                    }
                }
            });
            t.start();
            this.pool.add(t);
        });
    }

    public void execute(Runnable f) {
        this.mu.lock();
        try {
            this.queue.add(f);
            this.notEmpty.signal();
        } finally {
            this.mu.unlock();
        }
    }

    public void shutdown() {

    }

    public void awaitTermination() {

    }

}
