package com.gmail.alexei28.shortcutmultithreading.benchmarks;

/**
 * Запуск всех бенчмарков.
 *
 * Демонстрирует реальную разницу в производительности между:
 * - Последовательным выполнением
 * - FixedThreadPool с разным количеством потоков
 * - Virtual Threads (Java 21+)
 * - ForkJoinPool
 */
public class BenchmarkRunner {

    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║          JAVA CONCURRENCY BENCHMARKS                         ║");
        System.out.println("║          Java " + Runtime.version() + "                              ║");
        System.out.println("║          Cores: " + Runtime.getRuntime().availableProcessors() + "                                           ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝");
        System.out.println();

        // 1. CPU-bound бенчмарк
        System.out.println("Running CPU-bound benchmark...");
        CpuBoundBenchmark.run();
        System.out.println();

        // 2. IO-bound бенчмарк
        System.out.println("Running IO-bound benchmark...");
        IoBoundBenchmark.run();
        System.out.println();

        // 3. Overhead создания потоков
        System.out.println("Running thread creation overhead benchmark...");
        ThreadCreationBenchmark.run();
        System.out.println();

        // 4. Сравнение синхронизации
        System.out.println("Running synchronization benchmark...");
        SynchronizationBenchmark.run();
        System.out.println();

        System.out.println("╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║                    BENCHMARKS COMPLETE                       ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝");
    }
}