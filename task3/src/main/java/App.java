import pool.Pool;

void main() throws InterruptedException {

    Pool pool = new Pool(3);

    Thread.sleep(Duration.ofSeconds(7L));

    IntStream.range(0, 30).forEach(i -> pool.execute(() -> {
        long start = System.nanoTime();
        try {
            Thread.sleep(Duration.ofSeconds(1L));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return;
        }

        long waitedNanos = System.nanoTime() - start;
        Duration waited = Duration.ofNanos(waitedNanos);

        IO.println("Thread: " + i + " was done for " + waited.toMillis() + " ms");
    }));

    Thread.sleep(Duration.ofSeconds(7L));

    pool.shutdown();
    IO.println("Pool was shutdown");

    long start = System.nanoTime();
    pool.awaitTermination();

    long waitedNanos = System.nanoTime() - start;
    Duration waited = Duration.ofNanos(waitedNanos);

    IO.println("Wait graceful shutdown for: " + waited.toMillis() + " ms");
}
