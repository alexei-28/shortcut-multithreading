package com.gmail.alexei28.shortcutmultithreading.benchmarks;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * Бенчмарк для IO-bound задач.
 *
 * IO-bound задачи — это задачи, где основное время поток ждет:
 * - Ответа от сети (HTTP, DB)
 * - Чтения/записи на диск
 * - Ввода от пользователя
 *
 * Ожидаемые результаты:
 * - FixedThreadPool(4) — медленно, потоки заблокированы
 * - FixedThreadPool(100+) — быстро, но дорого по памяти (~1MB на поток)
 * - Virtual Threads — быстро и дешево (миллионы потоков)
 */
public class IoBoundBenchmark {

    private static final int TASK_COUNT = 500;
    private static final int IO_DELAY_MS = 100;  // Имитация сетевой задержки

    public static void run() {
        System.out.println("┌────────────────────────────────────────────────────────────┐");
        System.out.println("│ IO-BOUND BENCHMARK                                         │");
        System.out.println("│ Tasks: " + TASK_COUNT + ", каждая ждет " + IO_DELAY_MS + "ms (имитация HTTP)              │");
        System.out.println("└────────────────────────────────────────────────────────────┘");

        // Теоретическое время для последовательного выполнения
        long theoreticalSequential = (long) TASK_COUNT * IO_DELAY_MS;
        System.out.printf("  Theoretical sequential: %,8d ms%n", theoreticalSequential);

        long fixed4Time = runWithFixedPool(4);
        System.out.printf("  FixedThreadPool(4):     %,8d ms  (x%.1f faster)%n",
                fixed4Time, (double) theoreticalSequential / fixed4Time);

        long fixed50Time = runWithFixedPool(50);
        System.out.printf("  FixedThreadPool(50):    %,8d ms  (x%.1f faster)%n",
                fixed50Time, (double) theoreticalSequential / fixed50Time);

        long fixed200Time = runWithFixedPool(200);
        System.out.printf("  FixedThreadPool(200):   %,8d ms  (x%.1f faster)%n",
                fixed200Time, (double) theoreticalSequential / fixed200Time);

        long virtualTime = runWithVirtualThreads();
        System.out.printf("  Virtual Threads:        %,8d ms  (x%.1f faster)%n",
                virtualTime, (double) theoreticalSequential / virtualTime);

        System.out.println();
        System.out.println("  ВЫВОД: Для IO-bound Virtual Threads — лучший выбор!");
        System.out.println("         FixedThreadPool(200) = 200MB памяти на стеки");
        System.out.println("         Virtual Threads = минимальный overhead");
    }

    private static long runWithFixedPool(int threads) {
        ExecutorService executor = Executors.newFixedThreadPool(threads);
        long start = System.currentTimeMillis();

        List<Future<?>> futures = new ArrayList<>();
        for (int i = 0; i < TASK_COUNT; i++) {
            futures.add(executor.submit(IoBoundBenchmark::ioTask));
        }

        waitForAll(futures);
        executor.shutdown();
        return System.currentTimeMillis() - start;
    }

    private static long runWithVirtualThreads() {
        long start = System.currentTimeMillis();

        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            List<Future<?>> futures = new ArrayList<>();
            for (int i = 0; i < TASK_COUNT; i++) {
                futures.add(executor.submit(IoBoundBenchmark::ioTask));
            }
            waitForAll(futures);
        }

        return System.currentTimeMillis() - start;
    }

    /**
     * Имитация IO-операции (HTTP запрос, чтение из БД).
     */
    private static void ioTask() {
        try {
            Thread.sleep(IO_DELAY_MS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
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