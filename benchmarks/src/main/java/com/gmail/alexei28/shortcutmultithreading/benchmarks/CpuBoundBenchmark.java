package com.gmail.alexei28.shortcutmultithreading.benchmarks;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * Бенчмарк для CPU-bound задач.
 *
 * CPU-bound задачи — это задачи, где основное время тратится на вычисления,
 * а не на ожидание (IO, сеть и т.д.).
 *
 * Ожидаемые результаты:
 * - Максимальное ускорение ≈ количество ядер
 * - Больше потоков, чем ядер = overhead от переключения контекста
 * - Virtual Threads не дают преимущества для CPU-bound
 */
public class CpuBoundBenchmark {

    private static final int TASK_COUNT = 50_000;
    private static final int CORES = Runtime.getRuntime().availableProcessors();

    public static void run() {
        System.out.println("┌────────────────────────────────────────────────────────────┐");
        System.out.println("│ CPU-BOUND BENCHMARK                                        │");
        System.out.println("│ Tasks: " + TASK_COUNT + ", каждая вычисляет простые числа до 1000       │");
        System.out.println("└────────────────────────────────────────────────────────────┘");

        long sequentialTime = runSequential();
        System.out.printf("  Sequential:            %,8d ms%n", sequentialTime);

        long fixed4Time = runWithFixedPool(4);
        System.out.printf("  FixedThreadPool(4):    %,8d ms  (x%.2f faster)%n",
                fixed4Time, (double) sequentialTime / fixed4Time);

        long fixedCoresTime = runWithFixedPool(CORES);
        System.out.printf("  FixedThreadPool(%d):   %,8d ms  (x%.2f faster)%n",
                CORES, fixedCoresTime, (double) sequentialTime / fixedCoresTime);

        long fixed100Time = runWithFixedPool(100);
        System.out.printf("  FixedThreadPool(100):  %,8d ms  (x%.2f faster)%n",
                fixed100Time, (double) sequentialTime / fixed100Time);

        long virtualTime = runWithVirtualThreads();
        System.out.printf("  Virtual Threads:       %,8d ms  (x%.2f faster)%n",
                virtualTime, (double) sequentialTime / virtualTime);

        long forkJoinTime = runWithForkJoin();
        System.out.printf("  ForkJoinPool:          %,8d ms  (x%.2f faster)%n",
                forkJoinTime, (double) sequentialTime / forkJoinTime);

        System.out.println();
        System.out.println("  ВЫВОД: Для CPU-bound оптимально потоков ≈ количество ядер (" + CORES + ")");
        System.out.println("         Больше потоков = overhead от context switching");
    }

    private static long runSequential() {
        long start = System.currentTimeMillis();
        for (int i = 0; i < TASK_COUNT; i++) {
            cpuIntensiveTask();
        }
        return System.currentTimeMillis() - start;
    }

    private static long runWithFixedPool(int threads) {
        ExecutorService executor = Executors.newFixedThreadPool(threads);
        long start = System.currentTimeMillis();

        List<Future<?>> futures = new ArrayList<>();
        for (int i = 0; i < TASK_COUNT; i++) {
            futures.add(executor.submit(CpuBoundBenchmark::cpuIntensiveTask));
        }

        waitForAll(futures);
        executor.shutdown();
        return System.currentTimeMillis() - start;
    }

    private static long runWithVirtualThreads() {
        long start = System.currentTimeMillis();

        List<Thread> threads = new ArrayList<>();
        for (int i = 0; i < TASK_COUNT; i++) {
            Thread t = Thread.ofVirtual().start(CpuBoundBenchmark::cpuIntensiveTask);
            threads.add(t);
        }

        for (Thread t : threads) {
            try {
                t.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        return System.currentTimeMillis() - start;
    }

    private static long runWithForkJoin() {
        ForkJoinPool pool = new ForkJoinPool(CORES);
        long start = System.currentTimeMillis();

        List<ForkJoinTask<?>> tasks = new ArrayList<>();
        for (int i = 0; i < TASK_COUNT; i++) {
            tasks.add(pool.submit(CpuBoundBenchmark::cpuIntensiveTask));
        }

        for (ForkJoinTask<?> task : tasks) {
            task.join();
        }

        pool.shutdown();
        return System.currentTimeMillis() - start;
    }

    /**
     * CPU-интенсивная задача: поиск простых чисел.
     */
    private static void cpuIntensiveTask() {
        int count = 0;
        for (int i = 2; i < 1000; i++) {
            if (isPrime(i)) {
                count++;
            }
        }
    }

    private static boolean isPrime(int n) {
        if (n < 2) return false;
        for (int i = 2; i * i <= n; i++) {
            if (n % i == 0) return false;
        }
        return true;
    }

    private static void waitForAll(List<Future<?>> futures) {
        for (Future<?> f : futures) {
            try {
                f.get();
            } catch (Exception e) {
                // ignore
            }
        }
    }
}