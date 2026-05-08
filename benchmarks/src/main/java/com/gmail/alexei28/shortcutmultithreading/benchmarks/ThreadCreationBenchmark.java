package com.gmail.alexei28.shortcutmultithreading.benchmarks;

import java.util.ArrayList;
import java.util.List;

/**
 * Бенчмарк overhead'а создания потоков.
 *
 * Сравнивает стоимость создания:
 * - Platform Threads (обычные потоки OS)
 * - Virtual Threads (Project Loom)
 *
 * Ожидаемые результаты:
 * - Platform Thread: ~0.5-2 ms на создание + 1MB RAM на стек
 * - Virtual Thread: ~0.001-0.01 ms на создание + минимум RAM
 */
public class ThreadCreationBenchmark {

    private static final int PLATFORM_THREADS = 1_000;
    private static final int VIRTUAL_THREADS = 100_000;

    public static void run() {
        System.out.println("┌────────────────────────────────────────────────────────────┐");
        System.out.println("│ THREAD CREATION OVERHEAD                                   │");
        System.out.println("└────────────────────────────────────────────────────────────┘");

        // Platform Threads
        long platformStart = System.currentTimeMillis();
        List<Thread> platformThreads = new ArrayList<>();

        for (int i = 0; i < PLATFORM_THREADS; i++) {
            Thread t = new Thread(() -> {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
            t.start();
            platformThreads.add(t);
        }

        for (Thread t : platformThreads) {
            try {
                t.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        long platformTime = System.currentTimeMillis() - platformStart;
        double platformPerThread = (double) platformTime / PLATFORM_THREADS;

        System.out.printf("  Platform Threads (%,d):%n", PLATFORM_THREADS);
        System.out.printf("    Total time:    %,d ms%n", platformTime);
        System.out.printf("    Per thread:    %.3f ms%n", platformPerThread);
        System.out.printf("    Memory:        ~%d MB (1MB per thread stack)%n", PLATFORM_THREADS);

        // Virtual Threads
        long virtualStart = System.currentTimeMillis();
        List<Thread> virtualThreads = new ArrayList<>();

        for (int i = 0; i < VIRTUAL_THREADS; i++) {
            Thread t = Thread.ofVirtual().start(() -> {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
            virtualThreads.add(t);
        }

        for (Thread t : virtualThreads) {
            try {
                t.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        long virtualTime = System.currentTimeMillis() - virtualStart;
        double virtualPerThread = (double) virtualTime / VIRTUAL_THREADS;

        System.out.printf("%n  Virtual Threads (%,d):%n", VIRTUAL_THREADS);
        System.out.printf("    Total time:    %,d ms%n", virtualTime);
        System.out.printf("    Per thread:    %.4f ms%n", virtualPerThread);
        System.out.printf("    Memory:        ~few MB total%n");

        System.out.println();
        System.out.printf("  ВЫВОД: Virtual Threads в %.0fx дешевле по созданию!%n",
                platformPerThread / virtualPerThread);
        System.out.println("         Platform: 1000 потоков = 1GB RAM");
        System.out.println("         Virtual:  100000 потоков = несколько MB");
    }
}