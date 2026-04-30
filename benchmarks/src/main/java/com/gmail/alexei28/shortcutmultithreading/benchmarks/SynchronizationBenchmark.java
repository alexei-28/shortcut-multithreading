package com.gmail.alexei28.shortcutmultithreading.benchmarks;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.LongAdder;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Бенчмарк различных механизмов синхронизации.
 *
 * Сравнивает производительность:
 * - synchronized
 * - ReentrantLock
 * - AtomicInteger
 * - LongAdder
 *
 * Ожидаемые результаты:
 * - LongAdder — самый быстрый при высокой конкуренции
 * - AtomicInteger — быстрый при низкой конкуренции
 * - synchronized и ReentrantLock — примерно одинаковые
 */
public class SynchronizationBenchmark {

    private static final int THREADS = 8;
    private static final int INCREMENTS_PER_THREAD = 1_000_000;

    public static void run() {
        System.out.println("┌────────────────────────────────────────────────────────────┐");
        System.out.println("│ SYNCHRONIZATION BENCHMARK                                  │");
        System.out.println("│ " + THREADS + " потоков, " + String.format("%,d", INCREMENTS_PER_THREAD) + " инкрементов каждый                    │");
        System.out.println("└────────────────────────────────────────────────────────────┘");

        long syncTime = runSynchronized();
        System.out.printf("  synchronized:     %,8d ms%n", syncTime);

        long lockTime = runReentrantLock();
        System.out.printf("  ReentrantLock:    %,8d ms  (x%.2f vs sync)%n",
                lockTime, (double) syncTime / lockTime);

        long atomicTime = runAtomicInteger();
        System.out.printf("  AtomicInteger:    %,8d ms  (x%.2f vs sync)%n",
                atomicTime, (double) syncTime / atomicTime);

        long adderTime = runLongAdder();
        System.out.printf("  LongAdder:        %,8d ms  (x%.2f vs sync)%n",
                adderTime, (double) syncTime / adderTime);

        System.out.println();
        System.out.println("  ВЫВОД: LongAdder лучше всего для счетчиков при высокой конкуренции");
        System.out.println("         AtomicInteger хорош для простых атомарных операций");
        System.out.println("         synchronized/ReentrantLock для сложной логики");
    }

    // === synchronized ===
    private static int syncCounter = 0;
    private static final Object syncLock = new Object();

    private static long runSynchronized() {
        syncCounter = 0;
        ExecutorService executor = Executors.newFixedThreadPool(THREADS);
        CountDownLatch latch = new CountDownLatch(THREADS);

        long start = System.currentTimeMillis();

        for (int i = 0; i < THREADS; i++) {
            executor.submit(() -> {
                for (int j = 0; j < INCREMENTS_PER_THREAD; j++) {
                    synchronized (syncLock) {
                        syncCounter++;
                    }
                }
                latch.countDown();
            });
        }

        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        executor.shutdown();
        return System.currentTimeMillis() - start;
    }

    // === ReentrantLock ===
    private static int lockCounter = 0;
    private static final ReentrantLock reentrantLock = new ReentrantLock();

    private static long runReentrantLock() {
        lockCounter = 0;
        ExecutorService executor = Executors.newFixedThreadPool(THREADS);
        CountDownLatch latch = new CountDownLatch(THREADS);

        long start = System.currentTimeMillis();

        for (int i = 0; i < THREADS; i++) {
            executor.submit(() -> {
                for (int j = 0; j < INCREMENTS_PER_THREAD; j++) {
                    reentrantLock.lock();
                    try {
                        lockCounter++;
                    } finally {
                        reentrantLock.unlock();
                    }
                }
                latch.countDown();
            });
        }

        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        executor.shutdown();
        return System.currentTimeMillis() - start;
    }

    // === AtomicInteger ===
    private static final AtomicInteger atomicCounter = new AtomicInteger(0);

    private static long runAtomicInteger() {
        atomicCounter.set(0);
        ExecutorService executor = Executors.newFixedThreadPool(THREADS);
        CountDownLatch latch = new CountDownLatch(THREADS);

        long start = System.currentTimeMillis();

        for (int i = 0; i < THREADS; i++) {
            executor.submit(() -> {
                for (int j = 0; j < INCREMENTS_PER_THREAD; j++) {
                    atomicCounter.incrementAndGet();
                }
                latch.countDown();
            });
        }

        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        executor.shutdown();
        return System.currentTimeMillis() - start;
    }

    // === LongAdder ===
    private static final LongAdder longAdder = new LongAdder();

    private static long runLongAdder() {
        longAdder.reset();
        ExecutorService executor = Executors.newFixedThreadPool(THREADS);
        CountDownLatch latch = new CountDownLatch(THREADS);

        long start = System.currentTimeMillis();

        for (int i = 0; i < THREADS; i++) {
            executor.submit(() -> {
                for (int j = 0; j < INCREMENTS_PER_THREAD; j++) {
                    longAdder.increment();
                }
                latch.countDown();
            });
        }

        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        executor.shutdown();
        return System.currentTimeMillis() - start;
    }
}