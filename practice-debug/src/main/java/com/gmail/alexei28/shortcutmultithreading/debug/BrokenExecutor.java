package com.gmail.alexei28.shortcutmultithreading.debug;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * ЗАДАЧА: Найдите и исправьте утечку ресурсов.
 *
 * Проблема: ExecutorService создается, но никогда не закрывается.
 * Это приводит к утечке потоков и невозможности завершить JVM.
 *
 * Подсказка: Всегда закрывайте ExecutorService в finally-блоке
 * или используйте try-with-resources (Java 19+).
 */
public class BrokenExecutor {

    /**
     * Обрабатывает задачи параллельно.
     */
    public List<Integer> processInParallel(List<Integer> numbers) {
        ExecutorService executor = Executors.newFixedThreadPool(4);
        try {
            List<Future<Integer>> futures = new ArrayList<>();

            for (Integer num : numbers) {
                futures.add(executor.submit(() -> {
                    Thread.sleep(10);  // Имитация работы
                    return num * 2;
                }));
            }

            List<Integer> results = new ArrayList<>();
            for (Future<Integer> future : futures) {
                try {
                    results.add(future.get());
                } catch (Exception e) {
                    results.add(-1);
                }
            }
            return results;
        } finally {
            /*
                - shutdown() — мягкая остановка
                - awaitTermination() — ждём завершения
                - shutdownNow() — если зависли
                - восстанавливаем interrupt-флаг — очень важно
             */
            executor.shutdown();
            try {
                if (!executor.awaitTermination(1, TimeUnit.SECONDS)) {
                    executor.shutdownNow();
                }
            } catch (InterruptedException e) {
                executor.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * Еще один метод с той же проблемой.
     */
    public int processWithException(List<Integer> numbers) {
        ExecutorService executor = Executors.newFixedThreadPool(2);
        try {
            int sum = 0;
            for (Integer num : numbers) {
                if (num < 0) {
                    throw new IllegalArgumentException("Negative number: " + num);
                }
                sum += num;
            }
            return sum;
        } finally {
            executor.shutdown();
            try {
                if (!executor.awaitTermination(1, TimeUnit.SECONDS)) {
                    executor.shutdownNow();
                }
            } catch (InterruptedException e) {
                executor.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }
}