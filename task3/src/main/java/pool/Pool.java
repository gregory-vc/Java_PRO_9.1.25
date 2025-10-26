package pool;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.IntStream;


public class Pool {
    private static final Logger log = LoggerFactory.getLogger(Pool.class);
    private final LinkedList<Runnable> queue = new LinkedList<>();
    private final LinkedList<Thread> pool = new LinkedList<>();
    private final ReentrantLock mu = new ReentrantLock();
    private final Condition notEmpty = mu.newCondition();
    private boolean shutdown = false;

    public Pool(Integer size) {
        IntStream.range(0, size).forEach(_ -> {
            Thread t = new Thread(() -> {
                while (!Thread.currentThread().isInterrupted()) {
                    Runnable f;
                    try {
                        this.mu.lockInterruptibly();
                        try {
                            while (this.queue.isEmpty() && !this.shutdown) {
                                this.notEmpty.await();
                            }
                            if (this.queue.isEmpty()) {
                                return;
                            }
                            f = this.queue.removeFirst();
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
            if (this.shutdown) {
                throw new IllegalStateException("Pool already shutdown");
            }
            this.queue.add(f);
            this.notEmpty.signal();
        } finally {
            this.mu.unlock();
        }
    }

    public void shutdown() {
        this.mu.lock();
        try {
            this.shutdown = true;
            this.notEmpty.signalAll();
        } finally {
            this.mu.unlock();
        }
    }

    public void awaitTermination() {
        this.pool.forEach(t -> {
            try {
                t.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
    }
}
